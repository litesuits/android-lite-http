# Android网络通信框架LiteHttp 开篇简介和大纲目录

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com
QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

---

### 1. lite-http是什么？  (･̆⍛･̆)  

> LiteHttp是一款简单、智能、灵活的HTTP框架库，它在请求和响应层面做到了全自动构建和解析，主要用于Android快速开发。

### 2. 为什么选lite-http？  (•́ ₃ •̀) 

简单、强大：
```java
User user = liteHttp.get(url, User.class);
```
一行代码搞定API请求和数据转化。

案例详情可见我另一篇lite-http引言文章：
[LiteHttp 引言：开发者为什么要选LiteHttp？？][1]

### 3. lite-http有什么特点呀？    (´ڡ`)  
> 
轻量级：微小的内存开销与Jar包体积，99K左右。
> 
单线程：请求本身具有线程无关特性，基于当前线程高效率运作。
> 
全支持：GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS, PATCH。
> 
全自动：一行代码自动完成Model与Parameter、Json与Model。
> 
可配置：更多更灵活的配置选择项，多达 23+ 项。
> 
多态化：更加直观的API，输入和输出更加明确。
> 
强并发：自带强大的并发调度器，有效控制任务调度与队列控制策略。
> 
注解化：通过注解约定参数，URL、Method、ID、TAG等都可约定。
> 
易拓展：自定义DataParser将网络数据流转化为你想要的数据类型。
> 
可替换：基于接口，轻松替换网络连接实现方式和Json序列化库。
> 
多层缓存：内存命中更高效！多种缓存模式，支持设置缓存有效期。
> 
回调灵活：可选择当前或UI线程执行回调，开始结束、成败、上传、下载进度等都可监听。
> 
文件上传：支持单个、多个大文件上传。
> 
文件下载：支持文件、Bimtap下载及其进度通知。
> 
网络禁用：快速禁用一种、多种网络环境，比如指定禁用 2G，3G 。
> 
数据统计：链接、读取时长统计，以及流量统计。
> 
异常体系：统一、简明、清晰地抛出三类异常：客户端、网络、服务器，且异常都可精确细分。
> 
GZIP压缩：Request, Response 自动 GZIP 压缩节省流量。
> 
自动重试：结合探测异常类型和当前网络状况，智能执行重试策略。
> 
自动重定向：基于 30X 状态的重试，且可设置最大次数防止过度跳转。

### 4. lite-http的整体架构是怎样的呀？    (´ڡ`)  

![lite-http架构图][2]

关于App架构，请看我另一篇文章分享：
[怎样搭高质量的Android项目框架，框架的结构具体描述？][3]

### 5. 老湿，来点教学和分析带我飞呗？    (◕‸◕)  

好的 ◝‿◜ ，下面直接给你看，疗效好记得联系我，呵呵哒：

 > 
 1. [初始化和初步使用][4]
 2. [简化请求和非安全方法的使用][5]
 3. [自动对象转化][6]
 4. [自定义DataParser和Json序列化库的替换][7]
 5. [文件、位图的上传和下载][8]
 6. [禁用网络和流量、时间统计][9]
 7. [重试和重定向][10]
 8. [处理异常和取消请求][11]
 9. [POST方式的多种类型数据传输][12]
 10. [lite-http异步并发与调度策略][13]
 11. [全局配置与参数设置详解][14]
 12. [通过注解完成API请求][15]
 13. [多层缓存机制及用法][16]
 14. [回调监听器详解][17]
 15. [并发调度控制器详解][18]


  [1]: https://zybuluo.com/liter/note/186533
  [2]: http://litesuits.com/imgs/lite-http-arch.png
  [3]: https://zybuluo.com/liter/note/186526
  [4]: https://zybuluo.com/liter/note/186560
  [5]: https://zybuluo.com/liter/note/186561
  [6]: https://zybuluo.com/liter/note/186565
  [7]: https://zybuluo.com/liter/note/186583
  [8]: https://zybuluo.com/liter/note/186756
  [9]: https://zybuluo.com/liter/note/186801
  [10]: https://zybuluo.com/liter/note/186860
  [11]: https://zybuluo.com/liter/note/186900
  [12]: https://zybuluo.com/liter/note/186965
  [13]: https://zybuluo.com/liter/note/186998
  [14]: https://zybuluo.com/liter/note/187016
  [15]: https://zybuluo.com/liter/note/187568
  [16]: https://zybuluo.com/liter/note/187894
  [17]: https://zybuluo.com/liter/note/187904
  [18]: https://zybuluo.com/liter/note/189537