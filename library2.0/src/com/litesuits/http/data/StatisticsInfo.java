package com.litesuits.http.data;

import java.util.concurrent.atomic.AtomicLong;

public class StatisticsInfo {
    private AtomicLong connectTime = new AtomicLong();
    private AtomicLong dataLength  = new AtomicLong();

    public void addConnectTime(long time) {
        connectTime.addAndGet(time);
    }

    public void addDataLength(long len) {
        dataLength.addAndGet(len);
    }

    public long getConnectTime() {
        return connectTime.longValue();
    }

    public long getDataLength() {
        return dataLength.longValue();
    }

}