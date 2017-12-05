package com.nickchen.tokenhelper.Cache;

/**
 * 缓存的抽象类，方便实现多种缓存方式。
 * @author nickChen
 * 2017-12-04
 */
public interface IAccessTokenCache {
    // 默认超时时间7200秒 5秒用于程序执行误差
    int DEFAULT_TIME_OUT = 7200 - 5;

    String get(String key);

    void set(String key, String jsonValue);

    void remove(String key);

}
