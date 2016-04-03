# Android network framework: LiteHttp

Tags : litehttp2.x-tutorials

---
Website : http://litesuits.com

QQgroup : [42960650][1] , [47357508][2]

[Android网络通信为啥子选 lite-http ？][3]

[lite-http 初步使用 和 快速上手][4]

---

### 1. What‘s lite-http ? 

> LiteHttp is a simple, intelligent and flexible HTTP framework for Android. With LiteHttp you can make HTTP request with only one line of code! 
It could convert a java model to the parameter and rander the response JSON as a java model intelligently. 


### 2. Why choose lite-http ?

Simple, powerful, make HTTP request with only one line of code:
```Java
User user = liteHttp.get (url, User.class);
```

asynchronous download a file（execute on sub-thread，listen on ui-thread）:
```java
liteHttp.executeAsync(new FileRequest(url,path).setHttpListener(
	new HttpListener<File>(true, true, true) {
	
        @Override
        public void onLoading(AbstractRequest<File> request, long total, long len) {
            // loading notification
        }

        @Override
        public void onSuccess(File file, Response<File> response) {
            // successfully download 
        }
		
	})
);
```

configure an asynchronous login request by annotation:
```java
String loginUrl = "http://litesuits.com/mockdata/user_get";

// 1. URL        : loginUrl
// 2. Parameter  : name=value&password= value
// 3. Response   : User
@HttpUri(loginUrl) 
class LoginParam extends HttpRichParamModel<User> {
    private String name;
    private String password;

    public LoginParam(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
liteHttp.executeAsync(new LoginParam("lucy", "123456"));
```
will be built as **http://xxx?name=lucy&password=123456**

more details, you can see lite-http introduction: [LiteHttp Introduction: Why should developers choose LiteHttp ? ][5]


### 3. What are the fetures ?

- Lightweight: tiny size overhead to your app. About 99kb for core jar. .

- One-Thread-Based: all methods work on the same thread as the request was created.

- Full support: GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS, PATCH.

- Automatic: one line of code auto-complete translation between Model and Parameter, Json and Model.

- Configurable: more flexible configuration options, up to 23+ items.

- Polymorphic: more intuitive API, input and output is more clear.
 
- Strong Concurrency: concurrent scheduler that comes with a strong, effective control of scheduling and queue control strategies.

- Annotation Usage: convention over configuration. Parameters, Response, URL, Method, ID, TAG, etc. Can be configured.

- Easy expansion: extend the abstract class DataParser to parse inputstream(network) to which you want..

- Alternatively: interface-based, easy to replace the network connection implementations and Json serialization library.

- Multilayer cache: hit Memory is more efficient! Multiple cache mode. Support for setting cache expire time.

- Callback Flexible: callback can be on current or UI thread. listen the beginning, ending, success or failure, uploading, downloading, etc.

- File Upload: support for single, multiple, large file uploads.

- Downloads: support files, Bimtap download and progress notifications.

- Network Disabled: disable one of a variety of network environments, such as specifying disabling 2G, 3G.

- Statistics: time cost statistics and traffic statistics.

- Exception system: a unified, concise, clear exception is thrown into three categories: client, network, server, and abnormalities can be accurately subdivided.

- GZIP compression: automatic GZIP compression.

- Automatic Retry: combined probe exception type and current network conditions, intelligent retry strategies.

- Automatic redirection: based on the retry 30X state, and can set the maximum number of times to prevent excessive jump.


### 4. Overall architecture of lite-http

![Lite-http Chart][6]

About App architecture, see my other article:
[How to take high-quality Android project framework, the framework of the structure described in detail? ] [7]

### 5. tutorials and analysis (◕‸◕)

Good ◝‿◜, huh:

 [1. Initialization and preliminary usage] [8]

 [2. Simplified requests and non-safe method of use] [9]

 [3. Automatic model conversion] [10]

 [4. Custom DataParser and Json serialization library Replace] [11]

 [5. Files, bitmap upload and download] [12]

 [6. Disable network and traffic statistics] [13]

 [7. Retries and redirect] [14]

 [8. Exceptions handling and cancellation request] [15]

 [9. Multiple data transmission via POST(PUT)] [16]

 [10. Asynchronous concurrency and scheduling strategy] [17]

 [11. Global configuration and parameter settings Detailed] [18]

 [12. Annotation-Based request] [19]

 [13. Multilayer cache mechanism and usage] [20]

 [14. Detailed of callback listener] [21]

 [15. SmartExecutor： concurrent scheduler] [22]


## LiteHttp： Android网络通信框架

中文版 换个语种，再来一次

标签： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： [大群 47357508][1] ， [二群 42960650][2]

[Android网络框架为什么可以选用lite-http？][3]

[lite-http 初步使用 和 快速起步上手][4]

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

---

# LiteHttp之开篇简介和大纲目录

### 1. lite-http是什么？  (･̆⍛･̆)  

> LiteHttp是一款简单、智能、灵活的HTTP框架库，它在请求和响应层面做到了全自动构建和解析，主要用于Android快速开发。

### 2. 为什么选lite-http？  (•́ ₃ •̀) 

简单、强大，线程无关，一行代码搞定API请求和数据转化：
```java
User user = liteHttp.get(url, User.class);
```

当然也可以开启线程异步下载文件：
```java
liteHttp.executeAsync(new FileRequest(url,path).setHttpListener(
	new HttpListener<File>(true, true, true) {
	
        @Override
        public void onLoading(AbstractRequest<File> request, long total, long len) {
            // loading notification
        }

        @Override
        public void onSuccess(File file, Response<File> response) {
            // successfully download 
        }
		
	})
);
```

通过注解约定完成异步请求：
```java
@HttpUri(loginUrl) 
class LoginParam extends HttpRichParamModel<User> {
    private String name;
    private String password;

    public LoginParam(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
liteHttp.executeAsync(new LoginParam("lucy", "123456"));
```
将构建类似下面请求：http://xxx?name=lucy&password=123456

案例详情可见我另一篇lite-http引言文章：[LiteHttp 引言：开发者为什么要选LiteHttp？？][5]

### 3. lite-http有什么特点？    (´ڡ`)  

- 轻量级：微小的内存开销与Jar包体积，99K左右。

- 单线程：请求本身具有线程无关特性，基于当前线程高效率运作。

- 全支持：GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS, PATCH。

- 全自动：一行代码自动完成Model与Parameter、Json与Model。

- 可配置：更多更灵活的配置选择项，多达 23+ 项。

- 多态化：更加直观的API，输入和输出更加明确。
 
- 强并发：自带强大的并发调度器，有效控制任务调度与队列控制策略。

- 注解化：通过注解约定参数，URL、Method、ID、TAG等都可约定。

- 易拓展：自定义DataParser将网络数据流转化为你想要的数据类型。

- 可替换：基于接口，轻松替换网络连接实现方式和Json序列化库。

- 多层缓存：内存命中更高效！多种缓存模式，支持设置缓存有效期。

- 回调灵活：可选择当前或UI线程执行回调，开始结束、成败、上传、下载进度等都可监听。

- 文件上传：支持单个、多个大文件上传。

- 文件下载：支持文件、Bimtap下载及其进度通知。

- 网络禁用：快速禁用一种、多种网络环境，比如指定禁用 2G，3G 。

- 数据统计：链接、读取时长统计，以及流量统计。

- 异常体系：统一、简明、清晰地抛出三类异常：客户端、网络、服务器，且异常都可精确细分。

- GZIP压缩：Request, Response 自动 GZIP 压缩节省流量。

- 自动重试：结合探测异常类型和当前网络状况，智能执行重试策略。

- 自动重定向：基于 30X 状态的重试，且可设置最大次数防止过度跳转。


### 4. lite-http的整体架构是怎样的呀？    (´ڡ`)  

![lite-http架构图][6]

关于App架构，请看我另一篇文章分享：
[怎样搭高质量的Android项目框架，框架的结构具体描述？][7]

### 5. 老湿，来点教学和分析带我飞呗？    (◕‸◕)  

好的 ◝‿◜ ，下面直接给你看，疗效好记得联系我，呵呵哒：

 [1. 初始化和初步使用][8]

 [2. 简化请求和非安全方法的使用][9]
 
 [3. 自动对象转化][10]
 
 [4. 自定义DataParser和Json序列化库的替换][11]
 
 [5. 文件、位图的上传和下载][12]
 
 [6. 禁用网络和流量、时间统计][13]
 
 [7. 重试和重定向][14]
 
 [8. 处理异常和取消请求][15]
 
 [9. POST方式的多种类型数据传输][16]
 
 [10. lite-http异步并发与调度策略][17]
 
 [11. 全局配置与参数设置详解][18]
 
 [12. 通过注解完成API请求][19]
 
 [13. 多层缓存机制及用法][20]
 
 [14. 回调监听器详解][21]
 
 [15. 并发调度控制器详解][22]


  [1]: http://shang.qq.com/wpa/qunwpa?idkey=19bf15b9c85ec15c62141dd00618f725e2983803cd2b48566fa0e94964ae8370
  [2]: http://shang.qq.com/wpa/qunwpa?idkey=492d63aaffb04b23d8dc4df21f6b594008cbe1a819978659cddab2dbc397684e
  [3]: https://zybuluo.com/liter/note/186533
  [4]: https://zybuluo.com/liter/note/186560
  [5]: https://zybuluo.com/liter/note/186533
  [6]: http://litesuits.com/imgs/lite-http-arch.png
  [7]: https://zybuluo.com/liter/note/186526
  [8]: https://zybuluo.com/liter/note/186560
  [9]: https://zybuluo.com/liter/note/186561
  [10]: https://zybuluo.com/liter/note/186565
  [11]: https://zybuluo.com/liter/note/186583
  [12]: https://zybuluo.com/liter/note/186756
  [13]: https://zybuluo.com/liter/note/186801
  [14]: https://zybuluo.com/liter/note/186860
  [15]: https://zybuluo.com/liter/note/186900
  [16]: https://zybuluo.com/liter/note/186965
  [17]: https://zybuluo.com/liter/note/186998
  [18]: https://zybuluo.com/liter/note/187016
  [19]: https://zybuluo.com/liter/note/187568
  [20]: https://zybuluo.com/liter/note/187894
  [21]: https://zybuluo.com/liter/note/187904
  [22]: https://zybuluo.com/liter/note/189537
