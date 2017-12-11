package com.nickchen.tokenhelper;

/**
 * @author nickChen
 * @date create on 2017-12-11 10:23.
 */
public enum CacheType {
    /**
     * ConcurrentHashMap 实现的默认缓存策略
     */
    MAP,

    /**
     * Redis 实现缓存策略
     */
    REDIS
}
