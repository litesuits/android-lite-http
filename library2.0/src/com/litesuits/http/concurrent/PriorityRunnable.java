package com.litesuits.http.concurrent;

/**
 * @author MaTianyu
 * @date 2015-04-23
 */
public abstract class PriorityRunnable implements Runnable {

    int priority;

    protected PriorityRunnable(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
