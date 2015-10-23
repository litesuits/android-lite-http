#Android网络通信框架LiteHttp 第十四节：回调监听器详解

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---
#第十四节：LiteHttp之回调监听器详解

## 1. 开宗明义

本节讨论监听器 HttpListener ，将从以下几个方面了解它：
> 
- 回调时机
- 延时触发
- 安全回调
- 回调线程指定
- 全局监听器
- 级联监听器

说起lite-http的监听器，下面三个方面是主要内容：

1. 监听时机
 > 开始，成功，失败，取消，重试，上传进度，下载进度，结束

2. 全局监听
 > 监听全部的请求，每一个都尽在掌握。

3. 级联监听
 > 几乎可以无限级联，也就是可以设置一连串多个监听器。设想如既需要后台监听结果，又要前端监听进度，那级联监听就派上用场。


## 2. 回调时机
HttpListener 的回调时机有：

- onStart：开始
- onSuccess：成功
- onFailure：失败
- onCancel：取消
- onLoading：下载进度
- onUploading：上传进度
- onRetry：重试
- onRedirect：重定向
- onEnd：结束

> 
开始和结束作为开闭节点一定会被调用。
成功、失败、取消三个作为结果将有一个被调用。
下载进度通知仅在setReadingNotify为true时触发。
上传进度通知仅在setUploadingNotify为true时触发。

## 3. 延时触发

通过 setDelayMillis 方法设置回调触发时间的延迟。

这对于测试有很大帮助，如我要让请求耗时更长一些，好看下是否有异常。

再比如有时服务器响应很快，而你要看清楚加载动画是不是符合要求很难，这时加个3000毫秒的延迟触发，动画就看得很清楚了。

如果没有人工干预延迟，这些都是问题。

## 4. 安全回调

事情在不该发生的时间点上发生，会引起一些列异常。

网络请求也一样，比如请求比较耗时，页面都关掉销毁了，才拿到结果，显然不应该渲染界面了。

我建议继承 HttpListener 写一个自己的监听器，并复写 disableListener 方法在不合适的时机禁用回调，避免不必要的麻烦。

比如：
```java
class MyHttpListener<T> extends HttpListener<T> {
    private Activity activity;

    public MyHttpListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean disableListener() {
        return activity == null || activity.isFinishing();
    }

    @Override
    public void onFailure(HttpException e, Response response) {
        new MyHttpExceptHandler(activity).handleException(e);
    }
}
```
不仅在页面销毁之后禁用回调，而且失败时 **统一采用自定义的异常处理器**。它适用于Activity，Fragment里的监听器也一样的道理。

## 5. 回调线程指定

看一下监听器的构造函数：
```java
public HttpListener(boolean runOnUiThread) {
    setRunOnUiThread(runOnUiThread);
}

public HttpListener(boolean runOnUiThread, boolean readingNotify, boolean uploadingNotify) {
    this(runOnUiThread);
    this.readingNotify = readingNotify;
    this.uploadingNotify = uploadingNotify;
}
```
顾名思义：
> 
- runOnUiThread： 所有回调是否在主线程
- readingNotify： 是否开启下载进度通知
- uploadingNotify： 是否开启上传进度通知

当然也可以通过 setRunOnUiThread 方法来设置，true为主线程回调，false为当前线程回调。

## 6. 全局监听器

定义一个普通监听器：
```java
/**
 * global http listener for all request.
 */
GlobalHttpListener globalHttpListener = new GlobalHttpListener() {

    @Override
    public void onSuccess(Object data, Response<?> response) {
        HttpLog.i(TAG, "Global, request success ..." + data);
    }

    @Override
    public void onFailure(HttpException e, Response<?> response) {
        HttpLog.i(TAG, "Global, request failure ..." + e);
    }
};
```
监听所有请求：
```java
liteHttp.getConfig().setGlobalHttpListener(globalHttpListener);
```

## 7. 级联监听器

定义一个监听器：

```java
HttpListener<Bitmap> secondaryListener = new HttpListener<Bitmap>(true, true, true) {

    @Override
    public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
        HttpLog.i(TAG, " second listener, request success ...");
    }

    @Override
    public void onFailure(HttpException e, Response<Bitmap> response) {
        HttpLog.i(TAG, " second listener, request failure ...");
    }

};
```
定义新的监听器：
```java
HttpListener<Bitmap> firstHttpListener = new HttpListener<Bitmap>(false, false, false) {
    @Override
    public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
        HttpLog.i(TAG, "first Listener, request success ...");
    }

    @Override
    public void onFailure(HttpException e, Response<Bitmap> response) {
        HttpLog.i(TAG, "first Listener, request failure ...");
    }

    @Override
    public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
        HttpLog.i(TAG, "first Listener, request loading  ...");
    }
};
```
设置级联：
```java
// create a bitmap request.
BitmapRequest bitmapRequest = new BitmapRequest(picUrl);

// correct way to set first http listener
bitmapRequest.setHttpListener(firstHttpListener);
// correct way to set secondary (linked)listener
firstHttpListener.setLinkedListener(secondaryListener);

//load and show bitmap
liteHttp.executeAsync(bitmapRequest);
```
firstHttpListener先被调用，secondaryListener后被调用。注意不能循环级联，比如：
```java
firstHttpListener.setLinkedListener(secondaryListener);
secondaryListener.setLinkedListener(firstHttpListener);
```
这样就死循环了，框架会发现并抛出异常。

好的，这节比较长，到此结束，晚安~