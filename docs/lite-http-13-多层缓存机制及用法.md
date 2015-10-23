#Android网络通信框架LiteHttp 第十三节：多层缓存机制及用法

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---
#第十三节：LiteHttp之多层缓存机制及用法

先了解下什么是多级缓存，了解或做过图片加载框架的同学可能比较清楚，一般情况下第一级为闪存，第二级为外存，如果非要说第三级那么就是网络了，严格讲网络上的内容不算缓存。

lite-http设计之时也考虑到了这些，因为大多数API请求信息体不大，几K就算数据量比较大的了，这些请求完全可以缓存到内存。而有些请求占用空间非常大，比如图片、音乐等文件，对于网络框架而言，这些不适合存储到闪存，而需要缓存到本地SD卡等外存设备。

如何构建可缓存请求，请看下面代码：

```java
// Multi Cache Mechanism

StringRequest cacheRequest = new StringRequest(url);

cacheRequest.setCacheMode(CacheMode.CacheFirst);
cacheRequest.setCacheExpire(30, TimeUnit.SECONDS);
cacheRequest.setCacheDir("/sdcard/lite");
cacheRequest.setCacheKey(null);

cacheRequest.setHttpListener(new HttpListener<String>() {
    @Override
    public void onSuccess(String html, Response<String> response) {
        String title = response.isCacheHit() ? "Hit Cache(使用缓存)" : "No Cache(未用缓存)";
        HttpUtil.showTips(activity, title, html);
    }
});
liteHttp.executeAsync(cacheRequest);
```

主要代码为：
> 
response.isCacheHit()： 判断是否命中缓存
> 
setCacheExpire： 设置缓存有效时间，默认为-1，永久不超时。
setCacheDir： 设置放置缓存的文件夹
setCacheKey： 设置缓存文件名，不设置则框架自动产生。
setCacheMode： 设置缓存类型，CacheMode有四种，默认NetOnly方式。
> 
- NetOnly 即直接联网，不使用缓存；
- NetFirst 优先网络获取，失败后取缓存；
- CacheFirst 即优先用缓存，失败后连接网络；
- CacheOnly 即只使用缓存，不连接网络。


另外，还有一种通过注解设置缓存方式：

```java
@HttpUri(userUrl)
@HttpCacheMode(CacheMode.CacheFirst)
@HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES)
class UserAnnoParam implements HttpParamModel {
    public long id = 110;
    private String key = "aes";
}
```

或者
```java

// 其他更多注解还有：
@HttpUri(userUrl) // 定义uri 或者 path
@HttpCacheMode(CacheMode.CacheFirst) // 缓存模式
@HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES) // 缓存时间
class TEST extends HttpRichParamModel<User> { }

liteHttp.executeAsync(new TEST());
```

另外，lite-http还提供了一下方法来清除缓存：
> 
- 清除某个请求的缓存，包括闪存和文件缓存：
liteHttp.cleanCacheForRequest(request)
- 清除全部闪存缓存
liteHttp.clearMemCache()
- 删除当前缓存文件夹下面所有文件
liteHttp.deleteCachedFiles()


好，本节至此完。
