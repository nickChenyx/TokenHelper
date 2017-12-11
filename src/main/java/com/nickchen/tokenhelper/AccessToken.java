package com.nickchen.tokenhelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * 封装请求 token 的 json 返回体。
 *
 * @author nickChen
 * @date 2017/12/08
 */
public class AccessToken implements Serializable, RetryUtils.ResultCheck {

    private static final long serialVersionUID = -822464425433824314L;
    private static long INTERVAL_TIME_TO_REFRESH_TOKEN;

    private String accessToken;                 // access_token
    private Integer expiresIn;                  // expires_in 过期时间
    private String error;                       // 出错时有值
    private String errorDescription;            // 出错时有值，错误详细信息
    private String tokenType;                   // Bearer
    private Long expiredTime;                   // 存放过期的时间，获取到 Token 之后，通过 expires_in 计算得出。
    private String json;                        // 请求到的 json 体存在这儿

    static {
        String intervalTime = PropsUtil.get("intervalTimeToRefreshToken");
        try {
            if (intervalTime != null && !"".equals(intervalTime)) {
                INTERVAL_TIME_TO_REFRESH_TOKEN = Long.parseLong(intervalTime);
            } else {
                INTERVAL_TIME_TO_REFRESH_TOKEN = 30; // 默认提前 30秒刷新 token
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException(MessageFormat.format("配置文件 token.properties 的属性 intervalTimeToRefreshToken 应当设置为数字，代表了提前多少秒刷新TOKEN。错误输入：{0}", intervalTime));
        }
    }

    AccessToken(String jsonStr) {
        this.json = jsonStr;

        try {
            JSONObject temp = JSON.parseObject(jsonStr);
            accessToken = (String) temp.get("access_token");
            expiresIn = (Integer) temp.get("expires_in");
            error =  (String) temp.get("error");
            errorDescription = (String) temp.get("error_description");
            tokenType = (String) temp.get("token_type");
            if (expiresIn != null)
                expiredTime = System.currentTimeMillis() + ((expiresIn - INTERVAL_TIME_TO_REFRESH_TOKEN ) * 1000);
            // 用户缓存时还原
            if (temp.containsKey("expiredTime")) {
                expiredTime = (Long) temp.get("expiredTime");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getJson() {
        return json;
    }

    public String getCacheJson() {
        JSONObject temp = JSON.parseObject(json);
        temp.put("expiredTime", expiredTime);
        temp.remove("expires_in");
        return JSON.toJSONString(temp);
    }

    /**
     * !（过期时间为空 || 错误信息码不为空 || 过期时间小于当前时间） 且 token不为空
     *
     * @return Token 是否可用
     */
    public boolean isAvailable() {
        return !(expiredTime == null || error != null || expiredTime < System.currentTimeMillis()) && accessToken != null;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public boolean matching() {
        return isAvailable();
    }
}
