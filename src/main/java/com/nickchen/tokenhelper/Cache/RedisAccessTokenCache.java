package com.nickchen.tokenhelper.Cache;

import com.nickchen.tokenhelper.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * @author nickChen
 *         create on 2017-12-05 9:42.
 */
public class RedisAccessTokenCache implements IAccessTokenCache {

    private Logger LOG = LoggerFactory.getLogger(RedisAccessTokenCache.class);
    private final String ACCESS_TOKEN_PREFIX = "openapi:token:";

    private Jedis jedis;

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
