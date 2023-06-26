package com.xingyuv.jushauth.request;

import com.alibaba.fastjson.JSONObject;
import com.xingyuv.http.support.HttpHeader;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.config.AuthDefaultSource;
import com.xingyuv.jushauth.enums.AuthResponseStatus;
import com.xingyuv.jushauth.enums.AuthUserGender;
import com.xingyuv.jushauth.enums.scope.AuthOktaScope;
import com.xingyuv.jushauth.exception.AuthException;
import com.xingyuv.jushauth.model.AuthCallback;
import com.xingyuv.jushauth.model.AuthResponse;
import com.xingyuv.jushauth.model.AuthToken;
import com.xingyuv.jushauth.model.AuthUser;
import com.xingyuv.jushauth.utils.AuthScopeUtils;
import com.xingyuv.jushauth.utils.Base64Utils;
import com.xingyuv.jushauth.utils.HttpUtils;
import com.xingyuv.jushauth.utils.UrlBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Okta 登录
 * <p>
 * https://{domainPrefix}.okta.com/oauth2/default/.well-known/oauth-authorization-server
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @since 1.16.0
 */
public class AuthOktaRequest extends AuthDefaultRequest {

    public AuthOktaRequest(AuthConfig config) {
        super(config, AuthDefaultSource.OKTA);
    }

    public AuthOktaRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.OKTA, authStateCache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String tokenUrl = accessTokenUrl(authCallback.getCode());
        return getAuthToken(tokenUrl);
    }

    private AuthToken getAuthToken(String tokenUrl) {
        HttpHeader header = new HttpHeader()
                .add("accept", "application/json")
                .add("content-type", "application/x-www-form-urlencoded")
                .add("Authorization", "Basic " + Base64Utils.encode(config.getClientId().concat(":").concat(config.getClientSecret())));
        String response = new HttpUtils(config.getHttpConfig()).post(tokenUrl, null, header, false).getBody();
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        this.checkResponse(accessTokenObject);
        return AuthToken.builder()
                .accessToken(accessTokenObject.getString("access_token"))
                .tokenType(accessTokenObject.getString("token_type"))
                .expireIn(accessTokenObject.getIntValue("expires_in"))
                .scope(accessTokenObject.getString("scope"))
                .refreshToken(accessTokenObject.getString("refresh_token"))
                .idToken(accessTokenObject.getString("id_token"))
                .build();
    }

    @Override
    public AuthResponse refresh(AuthToken authToken) {
        if (null == authToken.getRefreshToken()) {
            return AuthResponse.builder()
                    .code(AuthResponseStatus.ILLEGAL_TOKEN.getCode())
                    .msg(AuthResponseStatus.ILLEGAL_TOKEN.getMsg())
                    .build();
        }
        String refreshUrl = refreshTokenUrl(authToken.getRefreshToken());
        return AuthResponse.builder()
                .code(AuthResponseStatus.SUCCESS.getCode())
                .data(this.getAuthToken(refreshUrl))
                .build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        HttpHeader header = new HttpHeader()
                .add("Authorization", "Bearer " + authToken.getAccessToken());
        String response = new HttpUtils(config.getHttpConfig()).post(userInfoUrl(authToken), null, header, false).getBody();
        JSONObject object = JSONObject.parseObject(response);
        this.checkResponse(object);
        JSONObject address = object.getJSONObject("address");
        return AuthUser.builder()
                .rawUserInfo(object)
                .uuid(object.getString("sub"))
                .username(object.getString("name"))
                .nickname(object.getString("nickname"))
                .email(object.getString("email"))
                .location(null == address ? null : address.getString("street_address"))
                .gender(AuthUserGender.getRealGender(object.getString("sex")))
                .token(authToken)
                .source(source.toString())
                .build();
    }

    @Override
    public AuthResponse revoke(AuthToken authToken) {
        Map<String, String> params = new HashMap<>(4);
        params.put("token", authToken.getAccessToken());
        params.put("token_type_hint", "access_token");

        HttpHeader header = new HttpHeader()
                .add("Authorization", "Basic " + Base64Utils.encode(config.getClientId().concat(":").concat(config.getClientSecret())));
        new HttpUtils(config.getHttpConfig()).post(revokeUrl(authToken), params, header, false);
        AuthResponseStatus status = AuthResponseStatus.SUCCESS;
        return AuthResponse.builder().code(status.getCode()).msg(status.getMsg()).build();
    }

    private void checkResponse(JSONObject object) {
        if (object.containsKey("error")) {
            throw new AuthException(object.getString("error_description"));
        }
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(String.format(source.authorize(), config.getDomainPrefix(), config.getAuthServerId()))
                .queryParam("response_type", "code")
                .queryParam("prompt", "consent")
                .queryParam("client_id", config.getClientId())
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("scope", this.getScopes(" ", true, AuthScopeUtils.getDefaultScopes(AuthOktaScope.values())))
                .queryParam("state", getRealState(state))
                .build();
    }

    @Override
    public String accessTokenUrl(String code) {
        return UrlBuilder.fromBaseUrl(String.format(source.accessToken(), config.getDomainPrefix(), config.getAuthServerId()))
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code")
                .queryParam("redirect_uri", config.getRedirectUri())
                .build();
    }

    @Override
    protected String refreshTokenUrl(String refreshToken) {
        return UrlBuilder.fromBaseUrl(String.format(source.refresh(), config.getDomainPrefix(), config.getAuthServerId()))
                .queryParam("refresh_token", refreshToken)
                .queryParam("grant_type", "refresh_token")
                .build();
    }

    @Override
    protected String revokeUrl(AuthToken authToken) {
        return String.format(source.revoke(), config.getDomainPrefix(), config.getAuthServerId());
    }

    @Override
    public String userInfoUrl(AuthToken authToken) {
        return String.format(source.userInfo(), config.getDomainPrefix(), config.getAuthServerId());
    }
}
