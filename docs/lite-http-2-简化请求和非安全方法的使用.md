# Android网络通信框架LiteHttp 第二节：简化请求和非安全方法的使用

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第二节：LiteHttp之简化请求和非安全方法的使用

方便测试，先随意定义一些合法的URL：
```java
public static final String url = "http://baidu.com";
public static final String httpsUrl = "https://baidu.com";
public static final String userUrl = "http://litesuits.com/mockdata/user_get";
```
## 1. 简化的请求

发起同步请求时，部分请求可以被简化。
GET方式获取API返回的String：
```java
 String html = liteHttp.get(url);
```

GET方式直接获取Java Model：
```java
 User user = liteHttp.get(userUrl, User.class);
```

POST方式获取String：
```java
 String result = liteHttp.post(new StringRequest(httpsUrl));
```

HEAD方式获取头信息：
```java
 NameValuePair[] headers = liteHttp.head(new StringRequest(url));
```

等等...可自行查看源码了解更多。

## 2. 非安全的请求

有时候开发者在某种情况下需要抛出异常中断后面代码，或者某场景下需要自己捕获异常，那么需要发送非安全的请求。

```java
// http scheme error
try {
    Response response = liteHttp.executeOrThrow(new BytesRequest("haha://hehe"));
    // do something...
} catch (HttpException e) {
    e.printStackTrace();
}

// java model translate error
try {
    User user = liteHttp.performOrThrow(new JsonAbsRequest<User>("http://thanku.love") {});
} catch (final HttpException e) {
    e.printStackTrace();
}
```

## 3. https 请求

```java
liteHttp.executeAsync(new StringRequest(httpsUrl).setHttpListener(
    new HttpListener<String>() {
        @Override
        public void onSuccess(String s, Response<String> response) {
            
        }
    }
));
```