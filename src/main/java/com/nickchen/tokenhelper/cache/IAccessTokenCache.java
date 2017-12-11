package com.nickchen.tokenhelper.cache;

/**
 * 缓存的抽象类，方便实现多种缓存方式。
 * <p>
 * 可以修改 {@code token.properties} 文件中的 {@code cacheType} 属性修改默认的 cache 实现。
 * <p>
 * {@code cacheType=map} 使用默认的缓存实现 {@link DefaultAccessTokenCache}
 * <p>
 * {@code cacheType=redis} 使用 Redis 作为缓存实现 {@link RedisAccessTokenCache}
 *
 * @author nickChen
 * @date 2017/12/08
 */
public interface IAccessTokenCache {
    // 默认超时时间7200秒 5秒用于程序执行误差
    int DEFAULT_TIME_OUT = 7200 - 5;

    String get(String key);

    void set(String key, String jsonValue);

    void remove(String key);

}
