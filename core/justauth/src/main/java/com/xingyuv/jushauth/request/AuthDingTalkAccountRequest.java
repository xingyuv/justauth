package com.xingyuv.jushauth.request;

import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.config.AuthDefaultSource;

/**
 * 钉钉账号登录
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @since 1.0.0
 */
public class AuthDingTalkAccountRequest extends AbstractAuthDingtalkRequest {

    public AuthDingTalkAccountRequest(AuthConfig config) {
        super(config, AuthDefaultSource.DINGTALK_ACCOUNT);
    }

    public AuthDingTalkAccountRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.DINGTALK_ACCOUNT, authStateCache);
    }
}
