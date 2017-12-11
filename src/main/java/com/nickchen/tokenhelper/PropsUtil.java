package com.nickchen.tokenhelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 用来读取配置文件的类
 *
 * @author nickChen
 * @date 2017/12/08
 */
public class PropsUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PropsUtil.class);
    private static final String PROPS_FILE_NAME = "token.properties";
    private static Properties props = new Properties();

    static {
        try {
            props.load(loadProperties());
        } catch (IOException  e) {
            LOG.error("读取 token.properties 错误，请检查参数是否完备或文件是否存在！");
            e.printStackTrace();
        } catch (NullPointerException ex) {
            LOG.error("读取 token.properties 错误，请检查参数是否完备或文件是否存在！");
            ex.printStackTrace();
        }
    }
    private static InputStream loadProperties() {
        /**
         * 读取配置文件顺序
         * 1. classpath 下的 token.properties 文件
         * 2. 当前 class 路径下的 token.properties 文件
         */
        InputStream inputStream = null;
        if ((inputStream = AccessTokenApi.class.getClassLoader().getResourceAsStream(PROPS_FILE_NAME)) != null) {
            return inputStream;
        } else if ((inputStream = AccessTokenApi.class.getResourceAsStream(PROPS_FILE_NAME)) != null) {
            return inputStream;
        } else {
            LOG.error("cannot find properties file -> ".concat(PROPS_FILE_NAME));
        }
        return null;
    }

    public static Properties getProps() {
        return props;
    }
    public static String get(String key) {
        return props.getProperty(key);
    }
}
