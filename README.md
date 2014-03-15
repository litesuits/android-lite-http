An Intelligent  Http Client
===
LiteHttp is a simple, intelligent and flexible HTTP client for Android. With LiteHttp you can make HTTP request with only one line of code! It supports GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS and PATCH request types. LiteHttp could convert a java model to the parameter of http request and rander the response JSON as a java model intelligently. And you can extend the abstract class DataParser to parse inputstream(network) to which you want.

Fetures
---
- **One thread**, all methods work on the same thread as the request was created.
- **Flexible architecture**, you can replace json library, apache httpclient or params builder easily.
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
![LiteHttp Architecture](litehttp_archi.png)
Basic Usage
---
###Basic Request
```java
LiteHttpClient client = ApacheHttpClient.getInstance(context);
Response res = client.execute(new Request("http://a.com"));
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
Man man = client.get(url, null, Man.class);
```
man json:
```json
{
    "name": "jame",
    "age": 26,
    "id": 18
}
```
### Multiple Files Upload Request
```java
	String url = "http://192.168.2.108:8080/LiteHttpServer/ReceiveFile";
	FileInputStream fis = new FileInputStream(new File("sdcard/1.jpg"));
	Request req = new Request(url);
	req.setMethod(HttpMethod.Post)
		.setParamModel(new BaiDuSearch())
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

### File and Bitmap load Request
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
###Star and Clone [LiteHttp](https://github.com/litesuits/android-lite-http) Github Project, Learn More Samples.
