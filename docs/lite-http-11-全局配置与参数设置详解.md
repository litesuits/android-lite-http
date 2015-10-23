#Android网络通信框架LiteHttp 第十一节：全局配置与参数设置详解

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---
#第十一节：LiteHttp之全局配置与参数设置详解

> lite-http可以设置全局监听器，全局默认请求方式等，便于统一进行管理和减少代码量。

可以进行的全局设置有：

- setDebugged：调试开关，打开后将会输出日志，App发布时记得关闭。

- setContext：设置上下文，可以是Activity、Service、Application等，用于判断网络状态和类型，以及获取App文件目录存放缓存。

- setUserAgent：设置全局默认User-Agent。

- setCommonHeaders ：设置全局的Header，所有请求都会带上这些参数。

-  setGlobalHttpListener：设置全局监听器，可以协助监听所有请求的各个过程和结果。

-  setGlobalSchemeHost：设置全局默认scheme和host，这样Request只需要设置API的路径Path即可。当Request已自带scheme时无视全局设置。

-  setDefaultCacheDir： 设置默认缓存存放文件夹，如果Request单独设置了缓存目录，则无视全局设置。

-  setDefaultCacheExpireMillis：设置全局默认的缓存超时时间，当Request已设置超时时间时无视全局设置，默认为-1，永久不超时。

-  setDefaultCacheMode：设置全局缓存方式，默认NetOnly方式。CacheMode有四种，NetOnly即直接联网，不使用缓存；NetFirst优先网络获取，失败后取缓存；CacheFirst即优先用缓存，失败后连接网络；CacheOnly即只使用缓存，不连接网络。

-  setDefaultCharSet：设置全局默认编码方式，不设置默认utf-8。

-  setDefaultHttpMethod：设置全局默认请求方式，默认GET。

-  setDefaultMaxRedirectTimes：设置全局默认重定向最大次数。

-  setDefaultMaxRetryTimes： 设置全局默认重试最大次数。

-  setDefaultModelQueryBuilder：设置全局默认参数构建器。

-  setDetectNetwork：设置连接前是否判断网络，若为true，判断无可用网络后直接结束请求。它需要设置context，才能有效。

-  setDisableNetworkFlags：设置全局禁用网络类型。

-  setDoStatistics：设置全局是否开启流量、耗时等统计。

-  setTimeOut：设置连接和读取超时时间。

-  setSocketBufferSize：设置读取流时缓存空间的大小。

-  setForRetry：设置重试时线程休眠时间，和是否强制重试。

-  setMaxMemCacheBytesSize：设置闪存缓存的最大空间，当满了时清除内存缓存。

-  setConcurrentSize：设置全局默认同时并发执行请求数量，建议设置数量为CPU核数。

-  setWaitingQueueSize：设置全局默认等待队列大小，当同时并发请求达到最大将进入等待队列，当执行中请求完成时，等待队列中的 请求按调度策略选择一个移除等待进入执行状态，等待请求过多超出此指标后将执行满载处理策略。

-  setSchedulePolicy：设置全局默认请求调度策略，SchedulePolicy有 LastInFirstRun后进先执行 和 FirstInFistRun先进先执行。

- setOverloadPolicy：设置全局默认满载处理策略，OverloadPolicy可选策略有 DiscardNewTaskInQueue抛弃队列中最新请求，DiscardOldTaskInQueue抛弃队列中最老的请求，DiscardCurrentTask抛弃当前请求，CallerRuns直接执行当前请求阻塞当前线程，ThrowExecption抛出异常中断当前线程。

关于一些名词：
> 
全局：即对每一个请求都生效。
默认：即请求没有配置此参数时，作为其默认值配置给它。

全局默认只是备胎，也就是缺省值，如果请求本身已经设置该参数则 **以其独立设置为准**，无视全局配置，遵循就近原则。

看下面代码：
```java
// Detail of Configuration

// init common headers for all request
List<NameValuePair> headers = new ArrayList<NameValuePair>();
headers.add(new NameValuePair("cookies", "this is cookies"));
headers.add(new NameValuePair("custom-key", "custom-value"));

HttpConfig newConfig = new HttpConfig(activity);

// app context(be used to detect network and get app files path)
newConfig.setContext(activity);
// the log is turn on when debugged is true
newConfig.setDebugged(true);
// set user-agent
newConfig.setUserAgent("Mozilla/5.0");
// set global scheme and host to all request.
newConfig.setGlobalSchemeHost("http://litesuits.com/");
// common headers will be set to all request
newConfig.setCommonHeaders(headers);
// set default cache path to all request
newConfig.setDefaultCacheDir(Environment.getExternalStorageDirectory() + "/a-cache");
// set default cache expire time to all request
newConfig.setDefaultCacheExpireMillis(30 * 60 * 1000);
// set default cache mode to all request
newConfig.setDefaultCacheMode(CacheMode.NetFirst);
// set default charset to all request
newConfig.setDefaultCharSet("utf-8");
// set default http method to all request
newConfig.setDefaultHttpMethod(HttpMethods.Get);
// set default maximum redirect-times to all request
newConfig.setDefaultMaxRedirectTimes(5);
// set default maximum retry-times to all request
newConfig.setDefaultMaxRetryTimes(1);
// set defsult model query builder to all request
newConfig.setDefaultModelQueryBuilder(new JsonQueryBuilder());
// whether to detect network before conneting.
newConfig.setDetectNetwork(true);
// disable some network
newConfig.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_NONE);
// whether open the traffic & time statistics
newConfig.setDoStatistics(true);
// set connect timeout: 10s,  socket timeout: 10s
newConfig.setTimeOut(10000, 10000);
// socket buffer size: 4096
newConfig.setSocketBufferSize(4096);
// if the network is unstable, wait 3000 milliseconds then start retry.
newConfig.setForRetry(3000, false);
// set global http listener to all request
newConfig.setGlobalHttpListener(null);
// set maximum size of memory cache space
newConfig.setMaxMemCacheBytesSize(1024 * 300);
// maximum number of concurrent tasks(http-request) at the same time
newConfig.setConcurrentSize(3);
// maximum number of waiting tasks(http-request) at the same time
newConfig.setWaitingQueueSize(100);
// set overload policy of thread pool executor
newConfig.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);
// set schedule policy of thread pool executor
newConfig.setSchedulePolicy(SchedulePolicy.LastInFirstRun);

// set a new config to lite-http
liteHttp.initConfig(newConfig);
```
通过上面的方法命名大多数开发者就能猜到其功能了，因为英文注释写的比较蹩脚，还是用中文都阐述了一遍。。。

爷已经很努力地用英文为代码写注释了，但有些意思还是不能完全的表达出来，中文解释下表达的比较好，中文对国内开发者更友好。