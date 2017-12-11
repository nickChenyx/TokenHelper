package com.nickchen.tokenhelper.cache;

import com.nickchen.tokenhelper.PropsUtil;
import com.nickchen.tokenhelper.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Redis 缓存的实现。
 * <p>
 * 可以修改 {@code token.properties} 中的 {@code accessTokenPrefix} 属性来自定义缓存前缀。
 *
 * @author nickChen
 * @date 2017/12/08
 */
public class RedisAccessTokenCache implements IAccessTokenCache {

    private Logger LOG = LoggerFactory.getLogger(RedisAccessTokenCache.class);
    private String ACCESS_TOKEN_PREFIX = "openapi:token:";
    private Jedis jedis;

    public RedisAccessTokenCache() {
        init();
    }

    private void init() {
        String prefix = PropsUtil.get("accessTokenPrefix");
        if (prefix != null && !"".equals(prefix)) {
            ACCESS_TOKEN_PREFIX = prefix;
        }
    }

    @Override
    public String get(String key) {
        jedis = RedisPool.getJedis();
        if (jedis != null) {
            return jedis.get(ACCESS_TOKEN_PREFIX.concat(key));
        }
        LOG.error("cannot get jedis instance. try again.");
        return null;
    }

    @Override
    public void set(String key, String value) {
        jedis = RedisPool.getJedis();
        if (jedis != null) {
            jedis.set(ACCESS_TOKEN_PREFIX.concat(key), value);
        }
    }

    @Override
    public void remove(String key) {
        jedis.del(ACCESS_TOKEN_PREFIX.concat(key));
    }
}
