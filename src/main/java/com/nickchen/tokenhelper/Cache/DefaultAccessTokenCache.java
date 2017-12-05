package com.nickchen.tokenhelper.Cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nickChen
 *         create on 2017-12-04 15:08.
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