package com.litesuits.http.concurrent;

/**
 * Policy of thread-pool-executor overload.
 *
 * @author MaTianyu
 * @date 2015-04-23
 */
public enum OverloadPolicy {
    DiscardNewTaskInQueue,
    DiscardOldTaskInQueue,
    DiscardCurrentTask,
    CallerRuns,
    ThrowExecption
}
