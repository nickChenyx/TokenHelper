package com.nickchen.tokenhelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 测试前提： OAauth 端已经将过期时间设置为30s。
 *
 * 测试思路：
 * 1. Token 获取是否成功
 * @see #testGetToken
 * 2. Token 是否被缓存
 * @see #testTokenCached()
 * 3. Token 过期是否会刷新
 * @see #testTokenExpired()
 *
 * @author nickChen
 * 2017-12-05
 */
public class AccessTokenApiTest {

    @Test
    public void testGetToken() {
        AccessToken accessToken = AccessTokenApi.getAccessToken();
        assertNotNull(accessToken);
    }

    @Test
    public void testTokenCached() throws InterruptedException {
        AccessToken a0 = AccessTokenApi.getAccessToken();
        Thread.sleep(5*1000);
        AccessToken a1 = AccessTokenApi.getAccessToken();
        assertEquals("Token 没有被缓存！", a0.getAccessToken(), a1.getAccessToken());
    }

    @Test
    public void testTokenExpired() throws InterruptedException {
        AccessToken a0 = AccessTokenApi.getAccessToken();
        Thread.sleep(30*1000);
        AccessToken a1 = AccessTokenApi.getAccessToken();
        assertNotEquals("Token 过期未替换！",a0.getAccessToken(), a1.getAccessToken());
    }
}
