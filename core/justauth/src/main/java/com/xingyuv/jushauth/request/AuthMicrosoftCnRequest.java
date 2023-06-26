package com.xingyuv.jushauth.request;

import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.config.AuthDefaultSource;

/**
 * 微软中国登录(世纪华联)
 *
 * @author mroldx (xzfqq5201314@gmail.com)
 * @since 1.16.4
 */
public class AuthMicrosoftCnRequest extends AbstractAuthMicrosoftRequest {

    public AuthMicrosoftCnRequest(AuthConfig config) {
        super(config, AuthDefaultSource.MICROSOFT_CN);
    }

    public AuthMicrosoftCnRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.MICROSOFT_CN, authStateCache);
    }

}
