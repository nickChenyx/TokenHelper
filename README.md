# Token Helper

> 本工具类旨在帮助解决 OAuth 客户端授权模式(*client_credentials*)下获取 `access_token`过期问题。
> 本工具类可以保持 `access_token`始终可用。

## 使用方法

**只需要一步 ->**  使用 `AccessTokenApi` 类的 `getAccessToken()` **静态方法**获取可用的 `access_token`。

---

## 配置


### 基于 properties 的文件配置

使用默认的 `token.properties` 文件，先扫描 classpath下的 `token.properties`文件。如果没有该文件，则扫描 tokenhelper 包路径下的 `token.properties`文件，即 `com/nickchen/tokenhelper/token.properties`。

### props文件配置客户端参数

在 `token.properties` 文件中，

```
# 获取token的url
url=http://localhost:1234/oauth/token
# 用户的 clientId
appId=LzWC4ihGWoMe5twOnjBm
# 用户的 clientSecret
appSecret=WK6nyYEVPLJxloDbxu7z
# 授权的 scope
scope=read
```

### 自定义缓存实现

在 `token.properties` 文件中，

```
# 使用 map作为 token缓存
cacheType=map
# 使用 redis作为 token缓存
# cacheType=redis
#
# 以下均为配置 cacheType为 redis之后才需要的配置。
#
# 配置 redis地址，默认是127.0.0.1
addr=127.0.0.1
# 配置 redis端口， 默认是6379
port=6379
# 配置访问密码，默认是null，无密码
pwd=
# redis pool的最大实例数
maxTotal=8
# redis pool的空闲实例数
maxIdle=8
# redis pool 等待可用连接的最大时间（毫秒）
maxWait=10000
# redis pool 连接超时时间（毫秒）
timeout=10000
# redis 中存储 token 的 key 前缀设置
accessTokenPrefix=openapi:token:
# 提前多少秒刷新 token
intervalTimeToRefreshToken=15
```
