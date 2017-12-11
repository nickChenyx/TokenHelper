package com.nickchen.tokenhelper;

import com.nickchen.tokenhelper.cache.DefaultAccessTokenCache;
import com.nickchen.tokenhelper.cache.IAccessTokenCache;
import com.nickchen.tokenhelper.cache.RedisAccessTokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Token 的 cache 的管理类。
 * 在这个类里决定 cache 的具体实现。
 *
 * @author nickChen
 * @date 2017/12/08
 */
public class TokenConfigKit {
    private static final Logger LOG = LoggerFactory.getLogger(TokenConfigKit.class);

    private static String cacheType = PropsUtil.get("cacheType");
    private static IAccessTokenCache accessTokenCache = null;

    static {
        // 如不设置缓存类型，默认使用 map
        if (cacheType == null) {
            cacheType = "map";
        }
        if (cacheType.equalsIgnoreCase(CacheType.MAP.name())) {
            accessTokenCache = new DefaultAccessTokenCache();
            LOG.info("正在使用 map 作为 token的缓存实现。");
        } else if (cacheType.equalsIgnoreCase(CacheType.REDIS.name())) {
            accessTokenCache = new RedisAccessTokenCache();
            LOG.info("正在使用 redis 作为 token的缓存实现。");
        } else {
            throw new RuntimeException("错误的 cacheType : ".concat(cacheType));
        }
    }
    public static void setAccessTokenCache(IAccessTokenCache accessTokenCache) {
        TokenConfigKit.accessTokenCache = accessTokenCache;
    }

    static IAccessTokenCache getAccessTokenCache() {
        return TokenConfigKit.accessTokenCache;
    }
}
