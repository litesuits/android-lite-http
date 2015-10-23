#Android网络通信框架LiteHttp 第七节：重试和重定向

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---
#第七节：LiteHttp之重试和重定向

## 1. 重试和重定向

先准备一个可以重定向的地址：
```java
 public String redirectUrl = "http://www.baidu.com/link?url=Lqc3GptP8u05JCRDsk0jqsAvIZh9WdtO_RkXYMYRQEm";
```
设置最大跳转和重试次数即可，然后开跳：
```java
// Retry/Redirect

// make request
StringRequest redirect = new StringRequest(redirectUrl)
        .setMaxRetryTimes(1) // maximum retry times
        .setMaxRedirectTimes(5) // maximum redirect times
        .setHttpListener(new HttpListener<String>() {

            @Override
            public void onRedirect(AbstractRequest<String> request, int max, int times) {
                Toast.makeText(activity, "Redirect max num: " + max + " , times: " + times
                                         + "\n GO-TO: " + request.getUri(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRetry(AbstractRequest<String> request, int max, int times) {
                Toast.makeText(activity, "Retry Now! max num: " + max + " , times: " + times
                        , Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSuccess(String s, Response<String> response) {
                HttpUtil.showTips(activity, "LiteHttp2.0", "Content Length: " + s.length());
            }
});

liteHttp.executeAsync(redirect);
```
也可以设置全局重试和重定向次数：
```java
// default retry times
liteHttp.getConfig().setDefaultMaxRetryTimes(2);
// default redirect times
liteHttp.getConfig().setDefaultMaxRedirectTimes(4);
```
如果请求本身设置了最大重试和重定向次数，以请求本身设置为准，如果未设置，则取全局默认值。

## 2. 关于重试的更多知识

重试出现的原因比较复杂或隐晦，网络状态不良时可能会触发，测试中难以必现。

当出现以下异常时，立即结束不作重试：
> 
UnknownHostException
FileNotFoundException
SSLException
ConnectException

当出现以下异常时，重试发生可能性较大：
> 
NoHttpResponseException
SocketException
SocketTimeoutException
ConnectTimeoutException

当未传入context时，线程睡眠指定时间，睡眠时间可以在config中设置，默认时间3000毫秒：
> 
// default retry waitting time
liteHttp.getConfig().setForRetry(1500, false);

传入context时，lite-http便可以自己判断网络状态，如果网络是连接中的线程不作睡眠立即重试，当系统网络处于连接中、扫描等状态时，睡眠等待指定时间后重试。

## 3. 关于重定向的原理和过程

当请求状态码为 **30x** 时，应该处理重定向的情况。
获取 header 中 location 字段的值，即为重定向新地址。
一般情况下新地址可能不包含 scheme 和 host 信息，因此需要我们自己拼接。
当重试大于最大次数，给予抛出异常处理。

框架主要代码如下：
```java
if (response.getRedirectTimes() < maxRedirectTimes) {
    // get the location header to find out where to redirect to
    Header locationHeader = ares.getFirstHeader(Consts.REDIRECT_LOCATION);
    if (locationHeader != null) {
        String location = locationHeader.getValue();
        if (location != null && location.length() > 0) {
            if (!location.toLowerCase().startsWith("http")) {
                URI uri = new URI(request.getFullUri());
                URI redirect = new URI(uri.getScheme(), uri.getHost(), location, null);
                location = redirect.toString();
            }
            response.setRedirectTimes(response.getRedirectTimes() + 1);
            request.setUri(location);
            if (HttpLog.isPrint) {
                HttpLog.i(TAG, "Redirect to : " + location);
            }
            if (listener != null) {
                listener.notifyCallRedirect(request, maxRedirectTimes, response.getRedirectTimes());
            }
            connectWithRetries(request, response);
            return;
        }
    }
    throw new HttpServerException(httpStatus);
} else {
    throw new HttpServerException(ServerException.RedirectTooMuch);
}
```

更多知识，多看源码，抽空造个小轮子，来得更直接。