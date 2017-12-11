package com.nickchen.tokenhelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常重试工具类
 *
 * @author nickChen
 * @date 2017/12/08
 */
public class RetryUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RetryUtils.class);
    /**
     * 回调结果检查
     */
    public interface ResultCheck {
        boolean matching();

        String getJson();
    }

    /**
     * 在遇到异常时尝试重试
     * @param retryLimit 重试次数
     * @param retryCallable 重试回调
     * @param <V> 泛型
     * @return V 结果
     */
    static <V extends ResultCheck> V retryOnException(int retryLimit,
                                                      java.util.concurrent.Callable<V> retryCallable) {
        V v = null;
        for (int i = 0; i < retryLimit; i++) {
            try {
                v = retryCallable.call();
            } catch (Exception e) {
                LOG.warn("retry on " + (i + 1) + " times v = " + (v == null ? null : v.getJson()) , e);
            }
            if (null != v && v.matching()) break;
            LOG.error("retry on " + (i + 1) + " times but not matching v = " + (v == null ? null : v.getJson()));
        }
        return v;
    }

    /**
     * 在遇到异常时尝试重试
     * @param retryLimit 重试次数
     * @param sleepMillis 每次重试之后休眠的时间
     * @param retryCallable 重试回调
     * @param <V> 泛型
     * @return V 结果
     * @throws InterruptedException 线程异常
     */
    public static <V extends ResultCheck> V retryOnException(int retryLimit, long sleepMillis,
            java.util.concurrent.Callable<V> retryCallable) throws InterruptedException {

        V v = null;
        for (int i = 0; i < retryLimit; i++) {
            try {
                v = retryCallable.call();
            } catch (Exception e) {
                LOG.warn("retry on " + (i + 1) + " times v = " + (v == null ? null : v.getJson()) , e);
            }
            if (null != v && v.matching()) break;
            LOG.error("retry on " + (i + 1) + " times but not matching v = " + (v == null ? null : v.getJson()));
            Thread.sleep(sleepMillis);
        }
        return v;
    }

}