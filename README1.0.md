An Intelligent  Http Client
===
LiteHttp is a simple, intelligent and flexible HTTP client for Android. With LiteHttp you can make HTTP request with only one line of code! It supports GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS and PATCH request types. LiteHttp could convert a java model to the parameter of http request and rander the response JSON as a java model intelligently. And you can extend the abstract class DataParser to parse inputstream(network) to which you want.

LiteHttp中文简介
---
 http://litesuits.github.io/guide/http/intro.html 
其中描述了LiteHttp的功能、特点、案例，以及架构模型。
为什么是LiteHttp？
---
 http://litesuits.github.io/guide/http/get-start.html 
LiteHttp引言，一个案例告诉你它的强大之处。



Fetures
---
- **One thread**, all methods work on the same thread as the request was created. Never-Across-Thread. [See more about Asynchronous](https://github.com/litesuits/android-lite-async)
- **Flexible architecture**, you can replace json library, apache httpclient or params builder easily.
- **Lightweight**. Tiny size overhead to your app. About 86kb for core jar. 
- Multiple method support, **get, post, head, put, delete, trace, options, patch.**
- **Multipart file uploads** without additional jars or libraries.
- **File and bitmap downloads** support by the built-in DataParser. You can extend DataParser yourself and set it for Request to parse http inputstream to which you want fairly easily.
- **Intelligent model convert** based on **json**:  Java Object Model <-> Http Parameter; Http Response <-> Java Object Model
- **Automatic redirects** with a limited number of times.
- Automatic **gizp** request encoding and response decoding for fast requests.
- Networt detection. **Smart retries** optimized for spotty mobile connections. 
- **Deactivate one or more networks**, such as 2G, 3G.
- Concise and unified **exception** handling strategy.
- The built-in AsyncExecutor make you send **concurrent asynchronous** requests fairly easily. You can make asynchronous requests with your own AsyncTask([see more about ameliorative Async](https://github.com/litesuits/android-lite-async)) if you like.

Architectures
---
###A well-architected  app：
![App Architecture](http://litesuits.github.io/guide/img/app_archi.png)
- Bottom is non-business-related Framework, Libraries.
- Middle is business-related third party and main logic. 
- Top is View renders and business logic caller. 

This make you migrate your code across different device, such as phone,pad,tv easy. 

###LiteHttp Architectures
![LiteHttp Architecture](http://litesuits.github.io/guide/img/litehttp_archi.png)

Basic Usage
---
###Basic Request
```java
LiteHttpClient client = LiteHttpClient.getInstance(context);
Response res = client.execute(new Request("http://baidu.com"));
String html = res.getString();
```
###Asynchronous Request
```java
HttpAsyncExcutor asyncExcutor = new HttpAsyncExcutor();
asyncExcutor.execute(client, new Request(url), new HttpResponseHandler() {
	@Override
	protected void onSuccess(Response res, HttpStatus status, NameValuePair[] headers) {
		// do some thing on UI thread
	}

	@Override
	protected void onFailure(Response res, HttpException e) {
		// do some thing on UI thread 
	}
});
```
###Java Model Parametered Requset
```java
// build a request url as :  http://a.com?name=jame&id=18
Man man = new Man("jame",18);
Response resonse = client.execute(new Request("http://a.com",man));
```
man class:
```java
public class Man implements HttpParam{
	private String name;
	private int id;
    private int age;
	public Man(String name, int id){
		this.name = name;
		this.id= id;
	}
}
```
###Intelligent Response Json Convert
```java
String url = "http://litesuits.github.io/mockdata/user?id=18";
User user = client.get(url, null, User.class);
```
User Class :
```java
public class User extends ApiResult {
	//全部声明public是因为写sample方便，不过这样性能也好，
	//即使private变量LiteHttp也能自动赋值，开发者可自行斟酌修饰符。
	public UserInfo data;

	public static class UserInfo {
		public String name;
		public int age;
		public ArrayList<String> girl_friends;
	}
}

public abstract class ApiResult {
	public String api;
	public String v;
	public Result result;

	public static class Result {
		public int code;
		public String message;
	}
}
```
User json structure:
```json
{
	"api": "com.xx.get.userinfo",
	"v": "1.0",
	"result": {
		"code": 200,
		"message": "success"
	},
	"data": {
		"age": 18,
		"name": "qingtianzhu",
		"girl_friends": [
			"xiaoli",
			"fengjie",
			"lucy"
		]
	}
}
```
### Multiple Files Upload Request
```java
	String url = "http://192.168.2.108:8080/LiteHttpServer/ReceiveFile";
	FileInputStream fis = new FileInputStream(new File("sdcard/1.jpg"));
	Request req = new Request(url);
	req.setMethod(HttpMethod.Post)
		.addParam("lite", new File("sdcard/lite.jpg"), "image/jpeg")
		.addParam("feiq", new File("sdcard/feiq.exe"), "application/octet-stream");
	if (fis != null) req.addParam("meinv", fis, "sm.jpg", "image/jpeg");
	Response res = client.execute(req);
```
### File and Bitmap load Request
```java
// one way
File file = client.execute(imageUrl, new FileParser("sdcard/lite.jpg"), HttpMethod.Get);
// other way
Response res = client.execute(new Request(imageUrl).setDataParser(new BitmapParser()));
Bitmap bitmap = res.getBitmap();
```

### Handle Exception(unified)
HttpException : ClientException + NetworkException + ServerException
```java
Request req = new Request(url).setMethod(HttpMethod.Head);
HttpAsyncExcutor asyncExcutor = new HttpAsyncExcutor();
asyncExcutor.execute(client, req, new HttpResponseHandler() {

	@Override
	public void onSuccess(Response response, HttpStatus status, NameValuePair[] headers) {
		response.getBitmap();
		// do some thing on ui thread
	}

	@Override
	public void onFailure(Response response, HttpException e) {

		new HttpExceptionHandler() {
			@Override
			protected void onClientException(HttpClientException e, ClientException type) {
				// Client Exception
			}

			@Override
			protected void onNetException(HttpNetException e, NetException type) {
				if (type == NetException.NetworkError) {
					// NetWork Unconnected
				} else if (type == NetException.UnReachable) {
					// NetWork UnReachable
				} else if (type == NetException.NetworkDisabled) {
					// Network Disabled
				}
			}

			@Override
			protected void onServerException(HttpServerException e, ServerException type, HttpStatus status, NameValuePair[] headers) {
				// Server Exception
			}

		}.handleException(e);
	}
});
```

关于作者（About Author）
-----
我的博客 ：[http://vmatianyu.cn](http://vmatianyu.cn/)

我的开源站点 ：[http://litesuits.com](http://litesuits.com/)

点击加入QQ群: [47357508](http://jq.qq.com/?_wv=1027&k=Z7l0Av)

我的论坛帖子
-----
[LiteHttp：极简且智能的 android HTTP 框架库 (专注于网络)](http://www.eoeandroid.com/thread-326584-1-1.html)

[LiteOrm：极简且智能的 android ORM 框架库 (专注数据库)](http://www.eoeandroid.com/thread-538203-1-1.html)

[LiteAsync：强势的 android 异步 框架库 (专注异步与并发)](http://www.eoeandroid.com/thread-538212-1-1.html)

[LiteCommon：丰富通用的android工具类库(专注于基础组件)](http://www.eoeandroid.com/thread-557246-1-1.html)

我的博客帖子
-----
[关于java的线程并发和锁的总结](http://www.vmatianyu.cn/summary-of-the-java-thread-concurrency-and-locking.html)

[android开发技术经验总结60条](http://www.vmatianyu.cn/summarization-of-technical-experience.html)

[聚划算android客户端1期教训总结](http://www.vmatianyu.cn/poly-effective-client-1-issues-lessons.html)

[移动互联网产品设计小结](http://www.vmatianyu.cn/summary-of-mobile-internet-product-design.html)

