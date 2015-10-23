#Android网络通信框架LiteHttp 第十节：异步并发与调度策略

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第十节：LiteHttp之异步并发与调度策略

lite-http 的异步执行和任务调度主要借助于 SmartExecutor  来完成的，关于 SmartExecutor 的介绍和使用后边会有专门的文章来讲解。

lite-http 因此具备 SmartExecutor 的全部特性：
> 
可定义核心并发线程数，即同一时间并发的请求数量。
> 
可定义等待排队线程数，即超出核心并发数后可排队请求数量。
> 
可定义等待队列进入执行状态的策略：先来先执行，后来先执行。
> 
可定义等待队列满载后处理新请求的策略：
> 
 - 抛弃队列中最新的任务
 - 抛弃队列中最旧的任务
 - 抛弃当前新任务
 - 直接执行（阻塞当前线程）
 - 抛出异常（中断当前线程）
 
 lite-http拥有一个独立的 SmartExecutor 的实例，在一个 App 中 SmartExecutor 可以有多个实例，每个实例都有独立核心和等待线程数指标，每个实例都有独立的调度和满载处理策略，但它们 **共享一个线程池**。这种机制既满足不同木块对线程控制和任务调度的独立需求，又共享一个池资源。独立又共享，最大程度上节省资源，提升性能。
 
心急的朋友又要喊了： shut up, show me the code!
```java
// Concurrent and Scheduling

HttpConfig httpConfig = liteHttp.getConfig();
// only one task can be executed at the same time
httpConfig.setConcurrentSize(1);
// at most two tasks be hold in waiting queue at the same time
httpConfig.setWaitingQueueSize(2);
// the last waiting task executed first
httpConfig.setSchedulePolicy(SchedulePolicy.LastInFirstRun);
// when task more than 3(current = 1, waiting = 2), new task will be discard.
httpConfig.setOverloadPolicy(OverloadPolicy.DiscardCurrentTask);
```
上面的代码对lite-http进行了并发方面的参数设置：
> 
核心并发数为 1
等待队列数为 2
调度采用 后进先执行 策略
满载时采用 抛弃新任务 策略

然后测试：
```java
for (int i = 0; i < 4; i++) {
    liteHttp.executeAsync(new StringRequest(url).setTag(i));
}

// submit order : 0 -> 1 -> 2 -> 3
// task 0 is executing,
// 1 and 2 is in waitting queue,
// 3 was discarded.
// real executed order: 0 -> 2 -> 1
```
上面的代码按顺序一次投入了四个请求，那么：
> 
投入任务顺序：0 -> 1 -> 2 -> 3
任务 0 立即执行
任务 1 和 2 在排队，按预定策略 2 后进，将先执行
任务 3 因为队列满载，按预定策略抛弃掉
实际执行顺序：0 -> 2 -> 1

对于 SmartExecutor，不论多少个实例，其控制指标和策略独立，始终共享一个池，避免掉多余开销。

理论上，**一个 App 只创建一个线程池** 就足够了，但是现在框架众多，有的独立精悍，有的功能众多，还是要提醒开发者，选用的框架最好知根知底、深入了解。不清楚里面什么机制，更是控制不了就会尴尬，到头来根本不知道自己的 App 里面有几个线程池。



开发者在构建自己的池子时要注意清闲时活跃线程不要多持，最好不要超过CPU数量，注意控制排队和满载策略，大量并发瞬间起来也能轻松应对。

另外，同时并发的线程数量不要过多，最好保持在CPU核数左右，过多了CPU时间片过多的轮转分配造成吞吐量降低，过少了不能充分利用CPU，并发数可以适当比CPU核数多一点没问题。

好了，本节先将这么多，更多并发姿势在关于 SmartExecutor 的文章里分享。




