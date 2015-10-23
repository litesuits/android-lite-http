#Android网络通信框架LiteHttp 第十二节：通过注解完成API请求

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---
#第十二节：LiteHttp之通过注解完成API请求

本节强烈推荐通过 RichParamModel 方式构建请求，它大概是长这样子：
```java
@HttpUri(loginUrl)
class LoginParam extends HttpRichParamModel<User> {
    public String name = "lucy";
    public String password = "123456";
}
liteHttp.executeAsync(new UserRichParam());
```
表示请求 loginUrl 这个地址，并传递 name 和 password 参数，返回用户 User 对象。

关于注解，还得从头说起：

在java模型自动转化那一节我们讲过，lite-http 可以把java对象转换为http请求的参数，需要该java类继承 HttpParamModel，实际上 HttpParamModel 还有一个拓展子类 HttpRichParamModel，拓展顾名思义是一个基于原来的增强，开发者连Request都不用写了，直接继承 HttpRichParamModel 通过注解来完成整个http请求的参数约定。

## 1. 简单参数 HttpParamModel

先看这段代码：
```java
// Usage of Annotation
public String userUrl = "http://litesuits.com/mockdata/user_get";
    
@HttpUri(userUrl)
@HttpMethod(HttpMethods.Get)
@HttpID(1)
@HttpCacheMode(CacheMode.CacheFirst)
@HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES)
class UserAnnoParam implements HttpParamModel {
    public long id = 110;
    private String key = "aes";
}

liteHttp.executeAsync(new JsonRequest<User>(new UserAnnoParam(), User.class) {}
        .setHttpListener(new HttpListener<User>() {
            @Override
            public void onSuccess(User user, Response<User> response) {
                HttpUtil.showTips(activity, "UserAnnoParam", user.toString());
            }
        }));
```

所见即所得，通过这些注解：
> 
@HttpUri(userUrl)
@HttpMethod(HttpMethods.Get)
@HttpID(1)
@HttpCacheMode(CacheMode.CacheFirst)
@HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES)

上面一段代码，完成了这样一次请求配置：
> 
请求地址为 userUrl
请求方式为 GET
请求ID为 1
请求优先使用缓存
请求缓存时间为1分钟

## 2. 扩展版参数HttpRichParamModel

再看一下拓展的参数类如何工作：
```java
// Best Practice: HTTP Rich Param Model (It is simpler and More Useful)

@HttpUri(userUrl)
class UserRichParam extends HttpRichParamModel<User> {
    public long id = 110;
    private String key = "aes";
}

// 一句话调用即可
liteHttp.executeAsync(new UserRichParam());
```

可见，最后一句执行时连 Request 都没有创建，直接将参数投入执行。
> 
定义RichParam，可指定URL、参数、响应体三个关键事物：
请求地址： uerUrl
请求参数： id=110 & key=aes
请求响应： User

更多注解方式有：
> 
@HttpSchemeHost("http://litesuits.com") // 定义scheme
@HttpUri("/mockdata/user_get") // 定义uri 或者 path
@HttpMethod(HttpMethods.Get) // 请求方式
@HttpCharSet("UTF-8") // 请求编码
@HttpTag("custom tag") // 打TAG
@HttpCacheMode(CacheMode.CacheFirst) // 缓存模式
@HttpCacheKey("custom-cache-key-name-by-myself") // 缓存文件名字
@HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES) // 缓存时间
@HttpID(2) // 请求ID
@HttpMaxRetry(3) // 重试次数
@HttpMaxRedirect(5) // 重定向次数

此外，继承Rich Param的参数类还可以复写下面方法：
> 
createHeaders ： 创建header
createQueryBuilder： 设置参数构建器
createHttpListener： 创建监听器
createHttpBody： 创建数据体

createHttpBody是为POST、PUT等方式创建数据体，GET、DELETE等不需要，因其参数直接拼接到URL。

## 3. 最佳实践
最后，通过这句：
> liteHttp.executeAsync(new UserRichParam());

可见 RichParamModel 是可以直接抛入执行的，这就将其和 Request 基本提升到了同一级别。
在我的 App 里，我是直接用 RichParamModel 的，因为它更直观简单，并且 RichParamModel 可以通过 set 方法设置监听器等参数，所以我也建议开发者使用这个进行请求配置。

强烈推荐通过 RichParamModel 方式构建请求。

