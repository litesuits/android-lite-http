# Android网络通信框架 LiteHttp2.0 实用教程

标签（空格分隔）： litehttp教程 android网络通信教程 android http http最佳实践

---

&nbsp;&nbsp;&nbsp;&nbsp;本系列文章面向各级别尤其中高级android开发者，将展示开源网络通信框架LiteHttp的核心用法，讲解其关键功能的运作原理。
&nbsp;&nbsp;&nbsp;&nbsp;希望可以让开发者既能熟练使用、修改开源HTTP框架高效完成日常开发任务，又能深入理解litehttp类库本身以及android网络通信相关知识。同时传达了一些框架作者在日常开发中的一些最佳实践，仅作抛砖引玉。

##第二节：异步与同步请求

###异步请求
异步请求有两种

```java
    // 1.0 init request
    final StringRequest request = new StringRequest(url).setHttpListener(
            new HttpListener<String>() {
                @Override
                public void onSuccess(String s, Response<String> response) {
                    HttpUtil.showTips(activity, "LiteHttp2.0", s);
                    response.printInfo();
                }
    
                @Override
                public void onFailure(HttpException e, Response<String> response) {
                    HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                }
            }
    );
    
    // 1.1 execute async
    liteHttp.executeAsync(request);
    
    // 1.2 perform async
    FutureTask<String> task = liteHttp.performAsync(request);
```

简单自定义配置：
```java
HttpConfig config = new HttpConfig(activity);

// set app context
config.setContext(activity);

// custom User-Agent
config.setUserAgent("Mozilla/5.0 (...)");

// connect timeout: 10s, socket timeout: 10s
config.setTimeOut(10000, 10000);

// new with config
LiteHttp liteHttp = LiteHttp.newApacheHttpClient(config);
```

这个案例示范了context（用于网络状态判断，获取网络类型），User-Agent,连接、读取超时参数配置。
更多的配置项有达23+项之多，非常的灵活，后边会有专门章节详细说明。

###初步使用

我们定义一个合法的http地址，如
```java
String url = "http://baidu.com"；
```

发起异步请求：
```java
liteHttp.executeAsync(new StringRequest(url));
```

异步获取原始byte：
```java
liteHttp.executeAsync(new BytesRequest(url));
```

异步加载一张位图：
```java
String saveToPath = "/sdcard/a.png";
liteHttp.executeAsync(new BitmapRequest(url,saveToPath));
```
saveToPath用来输入一个你指定的文件位置，位图将会保存到这里，不传入路径则分两种情况：
- 缓存未启用：仅载入内存，不做文件存储。
- 缓存开启：保存默认位置，默认存储位置和下面[下载文件]的规则一致。

异步下载一个文件：
```java
liteHttp.executeAsync(new FileRequest(url,saveToPath));
```
saveToPath用来输入一个你指定的文件位置，文件将会保存到这里，传入 null 则保存缓存默认位置。

默认位置规则：
- 如果为请求设置了 Cache-Key，则取其为文件名字
- 反之根据 Url 生成文件名字
文件夹位置在HttpConfig设置。

我们知道了怎么发起异步请求，那么请求是成功还是失败，成功如何获取结果，失败如何获取异常？

###处理结果
获取 String 的请求：
```java
liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
	@Override
	public void onSuccess(String s, Response<String> response) {
		// 成功：主线程回调，反馈一个string
	}

	@Override
	public void onFailure(HttpException e, Response<String> response) {
		// 失败：主线程回调，反馈异常
	}
}));
```
下载 File 的请求：
```java
liteHttp.executeAsync(new FileRequest(url,saveToPath).setHttpListener(
	new HttpListener<File>(true, true, true) {
	
		@Override
		public void onSuccess(File file, Response<File> response) {
		}
	
		@Override
		public void onFailure(HttpException e, Response<File> response) {
		}
		
		@Override
		public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
			// 进度通知
		}
	})
);
```
其他模式的请求，用法一样，这里不再多举。

值得注意的是 HttpListener 有多个参数可以设置：
HttpListener(boolean runOnUiThread, boolean readingNotify, boolean uploadingNotify)
分别用于设置：
- 开始、成功、失败、重试、上传、下载等所有回调是否在主线程（子线程回调性能更高，但不可操作UI）
- 是否开启下载进度通知
- 是否开启上传进度通知

注意，刚才提到，上传和下载同时是否在主线程被回调取决于第一个参数。
