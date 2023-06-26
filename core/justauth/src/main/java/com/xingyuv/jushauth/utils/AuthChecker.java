package com.xingyuv.jushauth.utils;

import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.config.AuthDefaultSource;
import com.xingyuv.jushauth.config.AuthSource;
import com.xingyuv.jushauth.model.AuthCallback;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.jushauth.enums.AuthResponseStatus;
import com.xingyuv.jushauth.exception.AuthException;

/**
 * 授权配置类的校验器
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @since 1.6.1-beta
 */
public class AuthChecker {

    /**
     * 是否支持第三方登录
     *
     * @param config config
     * @param source source
     * @return true or false
     * @since 1.6.1-beta
     */
    public static boolean isSupportedAuth(AuthConfig config, AuthSource source) {
        boolean isSupported = StringUtils.isNotEmpty(config.getClientId())
            && StringUtils.isNotEmpty(config.getClientSecret());
        if (isSupported && AuthDefaultSource.STACK_OVERFLOW == source) {
            isSupported = StringUtils.isNotEmpty(config.getStackOverflowKey());
        }
        if (isSupported && AuthDefaultSource.WECHAT_ENTERPRISE == source) {
            isSupported = StringUtils.isNotEmpty(config.getAgentId());
        }
        if (isSupported && (AuthDefaultSource.CODING == source || AuthDefaultSource.OKTA == source)) {
            isSupported = StringUtils.isNotEmpty(config.getDomainPrefix());
        }
        if (isSupported && AuthDefaultSource.XMLY == source) {
            isSupported = StringUtils.isNotEmpty(config.getDeviceId()) && null != config.getClientOsType();
            if (isSupported) {
                isSupported = config.getClientOsType() == 3 || StringUtils.isNotEmpty(config.getPackId());
            }
        }
        return isSupported;
    }

    /**
     * 检查配置合法性。针对部分平台， 对redirect uri有特定要求。一般来说redirect uri都是http://，而对于facebook平台， redirect uri 必须是https的链接
     *
     * @param config config
     * @param source source
     * @since 1.6.1-beta
     */
    public static void checkConfig(AuthConfig config, AuthSource source) {
        String redirectUri = config.getRedirectUri();
        if (config.isIgnoreCheckRedirectUri()) {
            return;
        }
        if (StringUtils.isEmpty(redirectUri)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, source);
        }
        if (!GlobalAuthUtils.isHttpProtocol(redirectUri) && !GlobalAuthUtils.isHttpsProtocol(redirectUri)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, source);
        }
        // facebook的回调地址必须为https的链接
        if (AuthDefaultSource.FACEBOOK == source && !GlobalAuthUtils.isHttpsProtocol(redirectUri)) {
            // Facebook's redirect uri must use the HTTPS protocol
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, source);
        }
        // 微软的回调地址必须为https的链接或者localhost,不允许使用http
        if (AuthDefaultSource.MICROSOFT == source && !GlobalAuthUtils.isHttpsProtocolOrLocalHost(redirectUri)) {
            // Microsoft's redirect uri must use the HTTPS or localhost
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, source);
        }
        // 微软中国的回调地址必须为https的链接或者localhost,不允许使用http
        if (AuthDefaultSource.MICROSOFT_CN == source && !GlobalAuthUtils.isHttpsProtocolOrLocalHost(redirectUri)) {
            // Microsoft's redirect uri must use the HTTPS or localhost
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, source);
        }
    }

    /**
     * 校验回调传回的code
     * <p>
     * {@code v1.10.0}版本中改为传入{@code source}和{@code callback}，对于不同平台使用不同参数接受code的情况统一做处理
     *
     * @param source   当前授权平台
     * @param callback 从第三方授权回调回来时传入的参数集合
     * @since 1.8.0
     */
    public static void checkCode(AuthSource source, AuthCallback callback) {
        // 推特平台不支持回调 code 和 state
        if (source == AuthDefaultSource.TWITTER) {
            return;
        }
        String code = callback.getCode();
        if (source == AuthDefaultSource.HUAWEI) {
            code = callback.getAuthorization_code();
        }
        if (StringUtils.isEmpty(code)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_CODE, source);
        }
    }

    /**
     * 校验回调传回的{@code state}，为空或者不存在
     * <p>
     * {@code state}不存在的情况只有两种：
     * 1. {@code state}已使用，被正常清除
     * 2. {@code state}为前端伪造，本身就不存在
     *
     * @param state          {@code state}一定不为空
     * @param source         {@code source}当前授权平台
     * @param authStateCache {@code authStateCache} state缓存实现
     */
    public static void checkState(String state, AuthSource source, AuthStateCache authStateCache) {
        // 推特平台不支持回调 code 和 state
        if (source == AuthDefaultSource.TWITTER) {
            return;
        }
        if (StringUtils.isEmpty(state) || !authStateCache.containsKey(state)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_STATUS, source);
        }
    }
}
