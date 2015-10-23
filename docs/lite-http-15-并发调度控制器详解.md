#Android网络通信框架LiteHttp 第十五节：并发调度控制器详解

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---
#第十五节：LiteHttp之并发调度控制器详解

## 1. 基本简介和使用
框架内置了一枚并发调度器，即SmartExecutor，不仅用来支持lite-http的异步并发支持，更可以直接投入 Runnable、Callable、FutureTask 等类型的运行任务。

可以用来作为 App 内支持异步并发的重要组件，在一个 App 中 SmartExecutor 可以有多个实例，每个实例都有独立核心和等待线程数指标，每个实例都有独立的调度和满载处理策略，但它们 **共享一个线程池**。这种机制既满足不同木块对线程控制和任务调度的独立需求，又共享一个池资源。独立又共享，最大程度上节省资源，提升性能。

SmartExecutor具有一下特性：

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
 
 看一下实例代码，初始化：
 
 ```java

// 智能并发调度控制器：设置[最大并发数]，和[等待队列]大小
SmartExecutor smallExecutor = new SmartExecutor();

// set temporary parameter just for test
// 一下参数设置仅用来测试，具体设置看实际情况。

// number of concurrent threads at the same time, recommended core size is CPU count
smallExecutor.setCoreSize(2);

// adjust maximum number of waiting queue size by yourself or based on phone performance
smallExecutor.setQueueSize(2);

// 任务数量超出[最大并发数]后，自动进入[等待队列]，等待当前执行任务完成后按策略进入执行状态：后进先执行。
smallExecutor.setSchedulePolicy(SchedulePolicy.LastInFirstRun);

// 后续添加新任务数量超出[等待队列]大小时，执行过载策略：抛弃队列内最旧任务。
smallExecutor.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);
 ```
 
上述代码设计了一个可同时并发2个线程，并发满载后等待队列可容纳2个线程，排队队列中后进的任务先执行，等待队列装满后新任务来到将抛弃队列中最老的任务。
 
测试多个线程并发的情况：
 ```java
// 一次投入 4 个任务
for (int i = 0; i < 4; i++) {
    final int j = i;
    smallExecutor.execute(new Runnable() {
        @Override
        public void run() {
            HttpLog.i(TAG, " TASK " + j + " is running now ----------->");
            SystemClock.sleep(j * 200);
        }
    });
}

// 再投入1个可能需要取消的任务
Future future = smallExecutor.submit(new Runnable() {
    @Override
    public void run() {
        HttpLog.i(TAG, " TASK 4 will be canceled... ------------>");
        SystemClock.sleep(1000);
    }
});

// 合适的时机取消此任务
future.cancel(false);
 ```
 
 上述代码，一次依次投入 0、1、2、3 四个任务，然后接着投入了新任务4，返回一个Future对象。
 
 根据设置，0、1会立即执行，执行满载后2、3进入排队队列，排队满载后独立投入的任务4来到，队列中最老的任务2被移除，队列中为3、4 。
 
 因为4随后被取消执行，所以最后输出：
 
```java
TASK 0 is running now ----------->
TASK 1 is running now ----------->
TASK 3 is running now ----------->
```

向我在另一篇文章中建议的，同时并发的线程数量不要过多，可以保持在CPU核数左右，并发线程过多了CPU时间片过多的轮转分配造成吞吐量降低，过少不能充分利用CPU，并发数可以适当比CPU核数多一点没问题。
 
## 2. 基本原理

我们看 SmartExecutor 的几个主要方法：
```java
public Future<?> submit(Runnable task)

public <T> Future<T> submit(Runnable task, T result)

public <T> Future<T> submit(Callable<T> task)

public <T> void submit(RunnableFuture<T> task)

public void execute(final Runnable command)
```

最主要的是 execute 方法，其他几个方法是将任务封装为 FutureTask 投入到 execute 方法执行。因为 FutureTask 本质就是一个 RunnableFuture 对象，兼具 Runnable 和 Future 的特性和功能。

那么重点就是看 execute 方法了：
```java
@Override
public void execute(final Runnable command) {
    if (command == null) {
        return;
    }

    WrappedRunnable scheduler = new WrappedRunnable() {
        @Override
        public Runnable getRealRunnable() {
            return command;
        }

        public Runnable realRunnable;

        @Override
        public void run() {
            try {
                command.run();
            } finally {
                scheduleNext(this);
            }
        }
    };

    boolean callerRun = false;
    synchronized (lock) {
        if (runningList.size() < coreSize) {
            runningList.add(scheduler);
            threadPool.execute(scheduler);
        } else if (waitingList.size() < queueSize) {
            waitingList.addLast(scheduler);
        } else {
            switch (overloadPolicy) {
                case DiscardNewTaskInQueue:
                    waitingList.pollLast();
                    waitingList.addLast(scheduler);
                    break;
                case DiscardOldTaskInQueue:
                    waitingList.pollFirst();
                    waitingList.addLast(scheduler);
                    break;
                case CallerRuns:
                    callerRun = true;
                    break;
                case DiscardCurrentTask:
                    break;
                case ThrowExecption:
                    throw new RuntimeException("Task rejected from lite smart executor. " + command.toString());
                default:
                    break;
            }
        }
        //printThreadPoolInfo();
    }
    if (callerRun) {
        command.run();
    }
}
```

可以看到整个过程简单概括为：
> 
1. 把任务封装为一个类似“链表”的结构体，执行完一个，调度下一个。
2. 加锁防止并发时抢夺资源，判断当前运行任务数量。
3. 当前任务数少于并发最大数量则投入运行，若满载则投入等待队列尾部。
4. 若等待队列未满新任务进入排队，若满则执行满载处理策略。
5. 当一个任务执行完，其尾部通过“链接”的方式调度一个新任务执行。若没有任务，则结束。

其中 加锁 和使用一个 WrappedRunnable 将任务包装成“链接”是重点。






