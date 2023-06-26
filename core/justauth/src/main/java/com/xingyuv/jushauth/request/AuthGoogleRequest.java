package com.xingyuv.jushauth.request;

import com.alibaba.fastjson.JSONObject;
import com.xingyuv.http.support.HttpHeader;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.config.AuthDefaultSource;
import com.xingyuv.jushauth.enums.AuthUserGender;
import com.xingyuv.jushauth.enums.scope.AuthGoogleScope;
import com.xingyuv.jushauth.exception.AuthException;
import com.xingyuv.jushauth.model.AuthCallback;
import com.xingyuv.jushauth.model.AuthToken;
import com.xingyuv.jushauth.model.AuthUser;
import com.xingyuv.jushauth.utils.AuthScopeUtils;
import com.xingyuv.jushauth.utils.HttpUtils;
import com.xingyuv.jushauth.utils.UrlBuilder;

/**
 * Google登录
 *
 * @author yangkai.shen (https://xkcoding.com)
 * @since 1.3.0
 */
public class AuthGoogleRequest extends AuthDefaultRequest {

    public AuthGoogleRequest(AuthConfig config) {
        super(config, AuthDefaultSource.GOOGLE);
    }

    public AuthGoogleRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.GOOGLE, authStateCache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String response = doPostAuthorizationCode(authCallback.getCode());
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        this.checkResponse(accessTokenObject);
        return AuthToken.builder()
                .accessToken(accessTokenObject.getString("access_token"))
                .expireIn(accessTokenObject.getIntValue("expires_in"))
                .scope(accessTokenObject.getString("scope"))
                .tokenType(accessTokenObject.getString("token_type"))
                .idToken(accessTokenObject.getString("id_token"))
                .build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.add("Authorization", "Bearer " + authToken.getAccessToken());
        String userInfo = new HttpUtils(config.getHttpConfig()).post(userInfoUrl(authToken), null, httpHeader).getBody();
        JSONObject object = JSONObject.parseObject(userInfo);
        this.checkResponse(object);
        return AuthUser.builder()
                .rawUserInfo(object)
                .uuid(object.getString("sub"))
                .username(object.getString("email"))
                .avatar(object.getString("picture"))
                .nickname(object.getString("name"))
                .location(object.getString("locale"))
                .email(object.getString("email"))
                .gender(AuthUserGender.UNKNOWN)
                .token(authToken)
                .source(source.toString())
                .build();
    }

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     * @since 1.9.3
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(super.authorize(state))
                .queryParam("access_type", "offline")
                .queryParam("scope", this.getScopes(" ", false, AuthScopeUtils.getDefaultScopes(AuthGoogleScope.values())))
                .queryParam("prompt", "select_account")
                .build();
    }

    /**
     * 返回获取userInfo的url
     *
     * @param authToken 用户授权后的token
     * @return 返回获取userInfo的url
     */
    @Override
    protected String userInfoUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo()).queryParam("access_token", authToken.getAccessToken()).build();
    }

    /**
     * 检查响应内容是否正确
     *
     * @param object 请求响应内容
     */
    private void checkResponse(JSONObject object) {
        if (object.containsKey("error") || object.containsKey("error_description")) {
            throw new AuthException(object.containsKey("error") + ":" + object.getString("error_description"));
        }
    }
}
