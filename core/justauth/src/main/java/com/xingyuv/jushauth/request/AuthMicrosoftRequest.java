package com.xingyuv.jushauth.request;

import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.config.AuthDefaultSource;

/**
 * 微软登录
 *
 * @author yangkai.shen (https://xkcoding.com)
 * @update:2021-08-24 mroldx (xzfqq5201314@gmail.com)
 * @since 1.5.0
 */
public class AuthMicrosoftRequest extends AbstractAuthMicrosoftRequest {

    public AuthMicrosoftRequest(AuthConfig config) {
        super(config, AuthDefaultSource.MICROSOFT);
    }

    public AuthMicrosoftRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.MICROSOFT, authStateCache);
    }

}
