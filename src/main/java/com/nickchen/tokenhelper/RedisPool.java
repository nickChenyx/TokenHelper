package com.nickchen.tokenhelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 连接池
 *
 * @author nickChen
 *         2017-12-05
 */
public final class RedisPool {
    private static String ADDR = "127.0.0.1";
    private static Integer PORT = 6379;
    /**
     * 访问密码
     */
    private static String AUTH = null;

    //可用连接实例的最大数目，默认为8；
    //如果赋值为-1，则表示不限制，如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
    private static Integer MAX_TOTAL = 8;
    //控制一个pool最多有多少个状态为idle(空闲)的jedis实例，默认值是8
    private static Integer MAX_IDLE = 8;
    //等待可用连接的最大时间，单位是毫秒，默认值为-1，表示永不超时。
    //如果超过等待时间，则直接抛出JedisConnectionException
    private static Integer MAX_WAIT_MILLIS = 10000;
    private static Integer TIMEOUT = 10000;
    //在borrow(用)一个jedis实例时，是否提前进行validate(验证)操作；
    //如果为true，则得到的jedis实例均是可用的
    private static Boolean TEST_ON_BORROW = true;
    private static JedisPool jedisPool = null;

    static {
        ADDR = getFromConf("addr") == null ? ADDR : getFromConf("addr");
        PORT = getFromConf("port") == null ? PORT : Integer.valueOf(getFromConf("port"));
        AUTH = getFromConf("pwd") == null ? AUTH : getFromConf("pwd");
        MAX_TOTAL = getFromConf("maxTotal") == null ? MAX_TOTAL : Integer.valueOf(getFromConf("maxTotal"));
        MAX_IDLE = getFromConf("maxIdle") == null ? MAX_IDLE : Integer.valueOf(getFromConf("maxIdle"));
        MAX_WAIT_MILLIS = getFromConf("maxWait") == null ? MAX_WAIT_MILLIS : Integer.valueOf(getFromConf("maxWait"));
        TIMEOUT = getFromConf("timeout") == null ? TIMEOUT : Integer.valueOf(getFromConf("timeout"));
    }
    private static String getFromConf(String key) {
        String value = PropsUtil.get(key);
        if (value == null || value.equals("")) {
            return null;
        }
        return value;
    }
    /**
     * 静态块，初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
        /*注意：
            在高版本的jedis jar包，比如本版本2.9.0，JedisPoolConfig没有setMaxActive和setMaxWait属性了
            这是因为高版本中官方废弃了此方法，用以下两个属性替换。
            maxActive  ==>  maxTotal
            maxWait==>  maxWaitMillis
         */
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis jedis = jedisPool.getResource();
                return jedis;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void returnResource(final Jedis jedis) {
        //方法参数被声明为final，表示它是只读的。
        if (jedis != null) {
            jedisPool.returnResource(jedis);
            //jedis.close()取代jedisPool.returnResource(jedis)方法将3.0版本开始
            //jedis.close();
        }
    }
}