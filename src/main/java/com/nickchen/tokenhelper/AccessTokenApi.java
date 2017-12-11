package com.nickchen.tokenhelper;

import com.nickchen.tokenhelper.cache.IAccessTokenCache;
import com.xiaoleilu.hutool.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 认证并获取 access_token。维持 access_token 可用。
 *
 * @author nickChen
 * @date 2017/12/08
 */
public class AccessTokenApi {
    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenApi.class);
    private static String url;
    private static String appId;
    private static String appSecret;
    private static String scope;

    static {
        try {
            url = PropsUtil.get("url");
            appId = PropsUtil.get("appId");
            appSecret = PropsUtil.get("appSecret");
            scope = PropsUtil.get("scope");
        } catch (NullPointerException e) {
            LOG.error("读取配置文件错误，请检查参数是否完备！");
            e.printStackTrace();
        }
    }

    /**
     * 从缓存中获取 access_token，如果未取到或者 access_token 不可用则先更新再获取
     *
     * @return AccessToken accessToken
     */
    static AccessToken getAccessToken() {
        AccessToken result = getAvailableAccessToken(appId, appSecret);
        if (result != null) {
            return result;
        }
        return refreshAccessTokenIfNecessary(appId, appSecret);
    }

    /**
     * 从缓存中获取 access_token。
     *
     * @param appId 客户端id
     * @param appSecret 客户端secret
     * @return {@link AccessToken}
     */
    private static AccessToken getAvailableAccessToken(String appId, String appSecret) {
        IAccessTokenCache accessTokenCache = TokenConfigKit.getAccessTokenCache();
        String accessTokenJson = accessTokenCache.get(appId);
        if (accessTokenJson != null && !"".equals(accessTokenJson)) {
            AccessToken result = new AccessToken(accessTokenJson);
            if (result.isAvailable()) {
                return result;
            }
        }
        return null;
    }

    /**
     * 直接获取 accessToken 字符串，方便使用
     *
     * @return String accessToken
     */
    public static String getAccessTokenStr() {
        return getAccessToken().getAccessToken();
    }

    /**
     * synchronized 配合再次获取 token 并检测可用性，防止多线程重复刷新 token 值
     * @param appId 客户端id
     * @param appSecret 客户端secret
     * @return {@link AccessToken}
     */
    private static synchronized AccessToken refreshAccessTokenIfNecessary(String appId, String appSecret) {
        AccessToken result = getAvailableAccessToken(appId, appSecret);
        if (result != null) {
            return result;
        }
        return refreshAccessToken(appId, appSecret);
    }

    /**
     * 无条件强制更新 access_token 值，不再对 cache 中的 token 进行判断
     *
     * @return AccessToken
     */
    public static AccessToken refreshAccessToken() {
        return refreshAccessToken(appId, appSecret);
    }

    /**
     * 无条件强制更新 access token 值，不再对 cache 中的 token 进行判断
     *
     * @return AccessToken
     */
    private static AccessToken refreshAccessToken(String appId, String appSecret) {
        final String postUrl = buildPostUrl();
        // 最多三次请求
        AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>() {
            @Override
            public AccessToken call() throws Exception {
                String json = HttpUtil.post(postUrl, "");
                return new AccessToken(json);
            }
        });

        if (null != result) {
            if (result.getAccessToken() == null) {
                throw new RuntimeException(result.getError().concat("\n").concat(result.getErrorDescription()));
            }
            // 利用 appId 与 accessToken 建立关联，支持多账户
            IAccessTokenCache accessTokenCache = TokenConfigKit.getAccessTokenCache();
            accessTokenCache.set(appId, result.getCacheJson());
        } else {
            throw new RuntimeException("Cannot get token from remote, please check config or contact manager.");
        }

        return result;
    }

    private static String buildPostUrl() {
        Map<String, String> queryParas = new HashMap<String, String>(4);
        queryParas.put("appId", appId);
        queryParas.put("appSecret", appSecret);
        queryParas.put("scope", scope);
        queryParas.put("grant_type", "client_credentials");
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        sb.append("client_id=");
        sb.append(queryParas.get("appId"));
        sb.append("&");
        sb.append("client_secret=");
        sb.append(queryParas.get("appSecret"));
        sb.append("&");
        sb.append("scope=");
        sb.append(queryParas.get("scope"));
        sb.append("&");
        sb.append("grant_type=");
        sb.append(queryParas.get("grant_type"));
        return sb.toString();
    }
}