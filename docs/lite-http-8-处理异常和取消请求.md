#Android网络通信框架LiteHttp 第八节：处理异常和取消请求

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第八节：LiteHttp之处理异常和取消请求

## 1. 处理异常

异步请求处理异常只需要在HttpListener的onFailure中着手即可，使用异常处理器：
```java
liteHttp.executeAsync(
        new StringRequest("httpa://invalid-url").setHttpListener(
        new HttpListener<String>() {
            @Override
            public void onFailure(HttpException exception, Response<String> response) {
            
                new HttpExceptionHandler() {
                    @Override
                    protected void onClientException(HttpClientException e, ClientException type) {
                    }

                    @Override
                    protected void onNetException(HttpNetException e, NetException type) {
                    }

                    @Override
                    protected void onServerException(HttpServerException e, ServerException type, HttpStatus status) {
                    }
                }.handleException(exception);
                
            }
        }));
```

## 2. 异常处理的最佳实践方式

下面介绍如何通过继承或者组合的方式来拓展、增强lite-http。

### 2.1 自定义异常处理器

推荐通过继承的手法来拓展，自建一个更强大的HttpExceptHandler来统一处理各类异常：

```java
// Best Practices of Exception Handling

public class MyHttpExceptHandler extends HttpExceptionHandler {
    private Activity activity;

    public MyHttpExceptHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onClientException(HttpClientException e, ClientException type) {
        switch (e.getExceptionType()) {
            case UrlIsNull:
                break;
            case ContextNeeded:
                // some action need app context
                break;
            case PermissionDenied:
                break;
            case SomeOtherException:
                break;
        }
        HttpUtil.showTips(activity, "LiteHttp2.0", "Client Exception:\n" + e.toString());
        activity = null;
    }

    @Override
    protected void onNetException(HttpNetException e, NetException type) {
        switch (e.getExceptionType()) {
            case NetworkNotAvilable:
                break;
            case NetworkUnstable:
                // maybe retried but fail
                break;
            case NetworkDisabled:
                break;
            default:
                break;
        }
        HttpUtil.showTips(activity, "LiteHttp2.0", "Network Exception:\n" + e.toString());
        activity = null;
    }

    @Override
    protected void onServerException(HttpServerException e, ServerException type,
                                     HttpStatus status) {
        switch (e.getExceptionType()) {
            case ServerInnerError:
                // status code 5XX error
                break;
            case ServerRejectClient:
                // status code 4XX error
                break;
            case RedirectTooMuch:
                break;
            default:
                break;
        }
        HttpUtil.showTips(activity, "LiteHttp2.0", "Server Exception:\n" + e.toString());
        activity = null;
    }
}
```
> 可以看到，增强版的 **异常处理器** 细分了各种异常，并且弹窗提示用户，开发者可根据产品调整细节。

### 2.2 自定义监听器

如果很多场景下都需要这个异常处理器来处理，那么推荐通过继承的手法来拓展HttpListener：
```java
class MyHttpListener<T> extends HttpListener<T> {
    private Activity activity;

    public MyHttpListener(Activity activity) {
        this.activity = activity;
    }

    // disable listener when activity is null or be finished.
    @Override
    public boolean disableListener() {
        return activity == null || activity.isFinishing();
    }

    // handle by this by call super.onFailure()
    @Override
    public void onFailure(HttpException e, Response response) {
        // handle exception 
        new MyHttpExceptHandler(activity).handleException(e);
    }
}

liteHttp.executeAsync(new StringRequest("httpa://invalid-url")
        .setHttpListener(new MyHttpListener<String>(activity){
            @Override
            public void onFailure(HttpException e, Response response) {
                super.onFailure(e, response);
                // 通过调用父类的处理方法，来调用 MyHttpExceptHandler 来处理异常。
            }
        }));
```
> 经过拓展的 **监听器** 有两点机智的地方：
> 
- 默认使用自定义的 **异常处理器** 统一处理异常
- 通过复写 disableListener 方法，使得在 Activity 为 null 或者 finish的情况下 **监听器** 的回调不再触发
> 
不仅仅统一处理，而且可以避免页面已销毁而请求终于也成功导致各种异常的尴尬。这相当于和 Activity 的生命周期绑定了，实际开发中很实用。

## 3. 正确地取消请求

lite-http框架设计之初就考虑到了请求可能随时被取消，因此及时地获取和判断请求是否取消，以及获取线程的状态。当请求被取消，或者线程被中断，那么lite-http将在最短时间内结束本次请求。

取消请求的第一种方式：

```java
StringRequest stringRequest = new StringRequest(url);

liteHttp.executeAsync(stringRequest);
SystemClock.sleep(100);

// one correct way is cancel this request
stringRequest.cancel();
```
取消请求的第二种方式，中断线程：

```java
StringRequest stringRequest = new StringRequest(url);

FutureTask futureTask = liteHttp.performAsync(stringRequest);
SystemClock.sleep(100);

// other correct way is interrupt this thread or task.
futureTask.cancel(true);
```

如果是同步的请求，直接调用 Thread 的 interrupt() 即可。

