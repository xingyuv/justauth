package com.xingyuv.jushauth.request;

import com.alibaba.fastjson.JSONObject;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.config.AuthDefaultSource;
import com.xingyuv.jushauth.enums.AuthResponseStatus;
import com.xingyuv.jushauth.exception.AuthException;
import com.xingyuv.jushauth.log.Log;
import com.xingyuv.jushauth.model.AuthCallback;
import com.xingyuv.jushauth.model.AuthResponse;
import com.xingyuv.jushauth.model.AuthToken;
import com.xingyuv.jushauth.model.AuthUser;
import com.xingyuv.jushauth.utils.AuthChecker;
import com.xingyuv.jushauth.utils.HttpUtils;
import com.xingyuv.jushauth.utils.UrlBuilder;

/**
 * <p>
 * 企业微信第三方二维码登录
 * </p>
 *
 * @author zhengjx
 * @since 1.16.3
 */
public class AuthWeChatEnterpriseThirdQrcodeRequest extends AbstractAuthWeChatEnterpriseRequest {
    public AuthWeChatEnterpriseThirdQrcodeRequest(AuthConfig config) {
        super(config, AuthDefaultSource.WECHAT_ENTERPRISE_QRCODE_THIRD);
    }

    public AuthWeChatEnterpriseThirdQrcodeRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.WECHAT_ENTERPRISE_QRCODE_THIRD, authStateCache);
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
                .queryParam("appid", config.getClientId())
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("state", getRealState(state))
                .queryParam("usertype", config.getUsertype())
                .build();
    }

    @Override
    public AuthResponse login(AuthCallback authCallback) {
        try {
            if (!config.isIgnoreCheckState()) {
                AuthChecker.checkState(authCallback.getState(), source, authStateCache);
            }
            AuthToken authToken = this.getAccessToken(authCallback);
            AuthUser user = this.getUserInfo(authToken);
            return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data(user).build();
        } catch (Exception e) {
            Log.error("Failed to login with oauth authorization.", e);
            return this.responseError(e);
        }
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        try {
            String response = doGetAuthorizationCode(accessTokenUrl());
            JSONObject object = this.checkResponse(response);
            AuthToken authToken = AuthToken.builder()
                    .accessToken(object.getString("provider_access_token"))
                    .expireIn(object.getIntValue("expires_in"))
                    .build();
            return authToken;
        } catch (Exception e) {
            throw new AuthException("企业微信获取token失败", e);
        }
    }

    @Override
    protected String doGetAuthorizationCode(String code) {
        JSONObject data = new JSONObject();
        data.put("corpid", config.getClientId());
        data.put("provider_secret", config.getClientSecret());
        return new HttpUtils(config.getHttpConfig()).post(accessTokenUrl(code), data.toJSONString()).getBody();
    }

    /**
     * 获取token的URL
     *
     * @return
     */
    protected String accessTokenUrl() {
        return UrlBuilder.fromBaseUrl(source.accessToken())
                .build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        JSONObject response = this.checkResponse(doGetUserInfo(authToken));
        return AuthUser.builder()
                .rawUserInfo(response)
                .build();
    }

    @Override
    protected String doGetUserInfo(AuthToken authToken) {
        JSONObject data = new JSONObject();
        data.put("auth_code", authToken.getCode());
        return new HttpUtils(config.getHttpConfig())
                .post(userInfoUrl(authToken), data.toJSONString()).getBody();
    }

    @Override
    protected String userInfoUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo())
                .queryParam("access_token", authToken.getAccessToken()).
                build();
    }

    private JSONObject checkResponse(String response) {
        JSONObject object = JSONObject.parseObject(response);
        if (object.containsKey("errcode") && object.getIntValue("errcode") != 0) {
            throw new AuthException(object.getString("errmsg"), source);
        }
        return object;
    }
}
