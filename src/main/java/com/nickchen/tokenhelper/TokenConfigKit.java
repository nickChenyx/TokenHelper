package com.nickchen.tokenhelper;

import com.nickchen.tokenhelper.Cache.DefaultAccessTokenCache;
import com.nickchen.tokenhelper.Cache.IAccessTokenCache;
import com.nickchen.tokenhelper.Cache.RedisAccessTokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Token 的 cache 的管理类。
 * 在这个类里决定 cache 的具体实现。
 * @author nickChen
 * 2017-12-04
 */
public class TokenConfigKit {
    private static final Logger LOG = LoggerFactory.getLogger(TokenConfigKit.class);

    private static String cacheType = PropsUtil.get("cacheType");
    static IAccessTokenCache accessTokenCache = null;

    static {
        // 如不设置缓存类型，默认使用 map
        if (cacheType == null) {
            cacheType = "map";
        }
        if (cacheType.equals("map")) {
            accessTokenCache = new DefaultAccessTokenCache();
            LOG.info("正在使用 map 作为 token的缓存实现。");
        } else if (cacheType.equals("redis")) {
            accessTokenCache = new RedisAccessTokenCache();
            LOG.info("正在使用 redis 作为 token的缓存实现。");
        } else {
            throw new RuntimeException("错误的 cacheType : ".concat(cacheType));
        }
    }
    public static void setAccessTokenCache(IAccessTokenCache accessTokenCache) {
        TokenConfigKit.accessTokenCache = accessTokenCache;
    }

    public static IAccessTokenCache getAccessTokenCache() {
        return TokenConfigKit.accessTokenCache;
    }
}
