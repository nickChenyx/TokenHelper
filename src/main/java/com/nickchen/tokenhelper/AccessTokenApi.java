package com.nickchen.tokenhelper;

import com.nickchen.tokenhelper.Cache.IAccessTokenCache;
import com.xiaoleilu.hutool.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 认证并获取 access_token API。
 * 维持 access_token 可用。
 *
 * @author nickChen
 *         2017年12月5日
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
     * 从缓存中获取 access token，如果未取到或者 access token 不可用则先更新再获取
     *
     * @return AccessToken accessToken
     */
    public static AccessToken getAccessToken() {
        AccessToken result = getAvailableAccessToken(appId, appSecret);
        if (result != null) {
            return result;
        }
        return refreshAccessTokenIfNecessary(appId, appSecret);
    }

    private static AccessToken getAvailableAccessToken(String appId, String appSecret) {
        IAccessTokenCache accessTokenCache = TokenConfigKit.getAccessTokenCache();
        String accessTokenJson = accessTokenCache.get(appId);
        if (accessTokenCache != null && !"".equals(accessTokenJson)) {
            AccessToken result = new AccessToken(accessTokenJson);
            if (result != null && result.isAvailable()) {
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
     */
    private static synchronized AccessToken refreshAccessTokenIfNecessary(String appId, String appSecret) {
        AccessToken result = getAvailableAccessToken(appId, appSecret);
        if (result != null) {
            return result;
        }
        return refreshAccessToken(appId, appSecret);
    }

    /**
     * 无条件强制更新 access token 值，不再对 cache 中的 token 进行判断
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
    public static AccessToken refreshAccessToken(String appId, String appSecret) {
        final String postUrl = buildPostUrl();
        // 最多三次请求
        AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>() {
            @Override
            public AccessToken call() throws Exception {
                String json = HttpUtil.post(postUrl, "");
                return new AccessToken(json);
            }
        });

        // 三次请求如果仍然返回了不可用的 access token 仍然 put 进去，便于上层通过 AccessToken 中的属性判断底层的情况
        if (null != result) {
            // 利用 appId 与 accessToken 建立关联，支持多账户
            IAccessTokenCache accessTokenCache = TokenConfigKit.getAccessTokenCache();
            accessTokenCache.set(appId, result.getCacheJson());
            if (result.getAccessToken() == null) {
                throw new RuntimeException(result.getError().concat("\n").concat(result.getError_description()));
            }
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

    public static void main(String[] args) throws InterruptedException {
        AccessToken old = null;
        Long start = System.currentTimeMillis();
        while (true) {
            Thread.sleep(500);
            AccessToken accessToken = AccessTokenApi.getAccessToken();
            if (old == null || !old.getAccessToken().equals(accessToken.getAccessToken())) {
                System.out.println(System.currentTimeMillis() - start);
                start = System.currentTimeMillis();
                old = accessToken;
            }
            System.out.print("token: " + accessToken.getAccessToken());
            System.out.println("     expiredTime: " + accessToken.getExpiredTime());
        }
    }

}