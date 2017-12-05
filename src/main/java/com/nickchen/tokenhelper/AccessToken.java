package com.nickchen.tokenhelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 封装 access_token
 */
@SuppressWarnings("unchecked")
public class AccessToken implements Serializable, RetryUtils.ResultCheck {
    private static final long serialVersionUID = -822464425433824314L;

    private String access_token;    // 正确获取到 access_token 时有值
    private Integer expires_in;        // 正确获取到 access_token 时有值
    private String error;        // 出错时有值
    private String error_description;            // 出错时有值
    private String token_type;      // Bearer

    private Long expiredTime;        // 正确获取到 access_token 时有值，存放过期时间
    private String json;

    public AccessToken(String jsonStr) {
        this.json = jsonStr;

        try {
            JSONObject temp = JSON.parseObject(jsonStr);
            access_token = (String) temp.get("access_token");
            expires_in = (Integer) temp.get("expires_in");
            error =  (String) temp.get("error");
            error_description = (String) temp.get("error_description");

            if (expires_in != null)
                expiredTime = System.currentTimeMillis() + ((expires_in - 5) * 1000);
            // 用户缓存时还原
            if (temp.containsKey("expiredTime")) {
                expiredTime = (Long) temp.get("expiredTime");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getJson() {
        return json;
    }

    public String getCacheJson() {
        JSONObject temp = JSON.parseObject(json);
        temp.put("expiredTime", expiredTime);
        temp.remove("expires_in");
        return JSON.toJSONString(temp);
    }

    public boolean isAvailable() {
        if (expiredTime == null)
            return false;
        if (error != null)
            return false;
        if (expiredTime < System.currentTimeMillis())
            return false;
        return access_token != null;
    }

    public String getAccessToken() {
        return access_token;
    }

    public Integer getExpiresIn() {
        return expires_in;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public String getError_description() {
        return error_description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    @Override
    public boolean matching() {
        return isAvailable();
    }
}
