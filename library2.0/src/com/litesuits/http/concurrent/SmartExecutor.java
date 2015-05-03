package com.litesuits.http.concurrent;

import android.util.Log;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.utils.HttpUtil;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A smart thread pool executor, about {@link SmartExecutor}:
 *
 * <ul>
 * <li>keep {@link #coreSize} tasks concurrent, and put them in {@link #runningList},
 * maximum number of running-tasks at the same time is {@link #coreSize}.</li>
 * <li>when {@link #runningList} is full, put new task in {@link #waitingQueue} waiting for execution,
 * maximum of waiting-tasks number is {@link #queueSize}.</li>
 * <li>when {@link #waitingQueue} is full, new task is performed by {@link OverloadPolicy}.</li>
 * <li>when running task is completed, take it out from {@link #runningList}.</li>
 * <li>schedule next by {@link SchedulePolicy}, take next task out from {@link #waitingQueue} to execute,
 * and so on until {@link #waitingQueue} is empty.</li>
 *
 * </ul>
 *
 * @author MaTianyu
 * @date 2015-04-23
 */
public class SmartExecutor implements Executor {
    private static final String TAG = SmartExecutor.class.getSimpleName();

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            HttpUtil.getCoresNumbers(),
            Integer.MAX_VALUE,
            10, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactory() {
                static final String NAME = "lite-";
                AtomicInteger IDS = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, NAME + IDS.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.DiscardPolicy());

    private int coreSize = HttpUtil.getCoresNumbers();
    private int queueSize = coreSize * 32;
    private final Object lock = new Object();
    private ArrayList<Runnable> runningList;
    private ArrayDeque<Runnable> waitingQueue;
    private SchedulePolicy schedulePolicy = SchedulePolicy.FirstInFistRun;
    private OverloadPolicy overloadPolicy = OverloadPolicy.DiscardOldTaskInQueue;


    public SmartExecutor() {
        waitingQueue = new ArrayDeque<Runnable>(queueSize);
        runningList = new ArrayList<Runnable>(coreSize);
    }

    public SmartExecutor(int coreSize, int queueSize) {
        this.coreSize = coreSize;
        this.queueSize = queueSize;
        waitingQueue = new ArrayDeque<Runnable>(queueSize);
        runningList = new ArrayList<Runnable>(coreSize);
    }

    /**
     * When {@link #execute(Runnable)} is called, {@link SmartExecutor} perform actions:
     * <ol>
     * <li>if fewer than {@link #coreSize} tasks are running, post new task in {@link #runningList} and execute it immediately.</li>
     * <li>if more than {@link #coreSize} tasks are running, and fewer than {@link #queueSize} tasks are waiting, put task in {@link #waitingQueue}.</li>
     * <li>if more than {@link #queueSize} tasks are waiting ,schedule new task by {@link OverloadPolicy}</li>
     * <li>if running task is completed, schedule next task by {@link SchedulePolicy} until {@link #waitingQueue} is empty.</li>
     * </ol>
     */
    @Override
    public void execute(final Runnable command) {
        if (command == null) {
            return;
        }
        Runnable scheduler = new Runnable() {
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
            if (HttpLog.isPrint) {
                HttpLog.v(TAG, "SmartExecutor core-queue size: " + coreSize + " - " + queueSize
                               + "  running-wait task: " + runningList.size() + " - " + waitingQueue.size());
            }
            if (runningList.size() < coreSize) {
                runningList.add(scheduler);
                threadPool.execute(scheduler);
                HttpLog.v(TAG, "SmartExecutor task execute");
            } else if (waitingQueue.size() < queueSize) {
                waitingQueue.addLast(scheduler);
                HttpLog.v(TAG, "SmartExecutor task waiting");
            } else {
                HttpLog.w(TAG, "SmartExecutor overload , policy is: " + overloadPolicy);
                switch (overloadPolicy) {
                    case DiscardNewTaskInQueue:
                        waitingQueue.pollLast();
                        waitingQueue.addLast(scheduler);
                        break;
                    case DiscardOldTaskInQueue:
                        waitingQueue.pollFirst();
                        waitingQueue.addLast(scheduler);
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
            HttpLog.w(TAG, "SmartExecutor task running in caller thread");
            command.run();
        }
    }

    private void scheduleNext(Runnable scheduler) {
        synchronized (lock) {
            boolean suc = runningList.remove(scheduler);
            if (HttpLog.isPrint) {
                HttpLog.v(TAG, "Thread " + Thread.currentThread().getName()
                               + " is completed. remove prior: " + suc + ", try schedule next..");
            }
            if (!suc) {
                runningList.clear();
                HttpLog.e(TAG,
                          "SmartExecutor scheduler remove failed, so clear all(running list) to avoid unpreditable error : " + scheduler);
            }
            if (waitingQueue.size() > 0) {
                Runnable waitingRun;
                switch (schedulePolicy) {
                    case LastInFirstRun:
                        waitingRun = waitingQueue.pollLast();
                        break;
                    case FirstInFistRun:
                        waitingRun = waitingQueue.pollFirst();
                        break;
                    default:
                        waitingRun = waitingQueue.pollLast();
                        break;
                }
                if (waitingRun != null) {
                    runningList.add(waitingRun);
                    threadPool.execute(waitingRun);
                    HttpLog.v(TAG, "Thread " + Thread.currentThread().getName() + " execute next task..");
                } else {
                    HttpLog.e(TAG,
                              "SmartExecutor get a NULL task from waiting queue: " + Thread.currentThread().getName());
                }
            } else {
                if (HttpLog.isPrint) {
                    HttpLog.v(TAG, "SmartExecutor: all tasks is completed. current thread: " +
                                   Thread.currentThread().getName());
                    //printThreadPoolInfo();
                }
            }
        }
    }

    public void printThreadPoolInfo() {
        if (HttpLog.isPrint) {
            Log.i(TAG, "______________________________________");
            Log.i(TAG, "state (shutdown - terminating - terminated): " + threadPool.isShutdown()
                       + " - " + threadPool.isTerminating() + " - " + threadPool.isTerminated());
            Log.i(TAG, "pool size (core - max): " + threadPool.getCorePoolSize()
                       + " - " + threadPool.getMaximumPoolSize());
            Log.i(TAG, "task (actice - complete - total): " + threadPool.getActiveCount()
                       + " - " + threadPool.getCompletedTaskCount() + " - " + threadPool.getTaskCount());
            Log.i(TAG, "waitingQueue size : " + threadPool.getQueue().size() + " , " + threadPool.getQueue());
        }
    }

    public int getCoreSize() {
        return coreSize;
    }

    /**
     * Set maximum number of concurrent tasks at the same time.
     * Recommended core size is CPU count.
     *
     * @param coreSize number of concurrent tasks at the same time
     * @return this
     */
    public SmartExecutor setCoreSize(int coreSize) {
        if (coreSize <= 0) {
            throw new NullPointerException("coreSize can not <= 0 !");
        }
        this.coreSize = coreSize;
        return this;
    }

    public int getQueueSize() {
        return queueSize;
    }

    /**
     * Adjust maximum number of waiting queue size based phone performance.
     * For example: CPU count * 32;
     *
     * @param queueSize waiting queue size
     * @return this
     */
    public SmartExecutor setQueueSize(int queueSize) {
        if (queueSize < 0) {
            throw new NullPointerException("queueSize can not < 0 !");
        }
        this.queueSize = queueSize;
        return this;
    }


    public OverloadPolicy getOverloadPolicy() {
        return overloadPolicy;
    }

    public void setOverloadPolicy(OverloadPolicy overloadPolicy) {
        if (overloadPolicy == null) {
            throw new NullPointerException("OverloadPolicy can not be null !");
        }
        this.overloadPolicy = overloadPolicy;
    }

    public SchedulePolicy getSchedulePolicy() {
        return schedulePolicy;
    }

    public void setSchedulePolicy(SchedulePolicy schedulePolicy) {
        if (schedulePolicy == null) {
            throw new NullPointerException("SchedulePolicy can not be null !");
        }
        this.schedulePolicy = schedulePolicy;
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

}
