package com.nickchen.tokenhelper.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的缓存实现。
 *
 * @author nickChen
 * @date 2017/12/08
 */
public class DefaultAccessTokenCache implements IAccessTokenCache {

    private Map<String, String> map = new ConcurrentHashMap<String, String>();

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void set(String key, String jsonValue) {
        map.put(key, jsonValue);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

}