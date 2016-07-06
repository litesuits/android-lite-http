package com.litesuits.http.listener;

import android.os.SystemClock;
import com.litesuits.http.data.StatisticsInfo;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.response.InternalResponse;
import com.litesuits.http.response.Response;

public class StatisticsListener {
    private static final String TAG = StatisticsListener.class.getSimpleName();
    private StatisticsInfo statisticsInfo;
    private InternalResponse internalResponse;
    private long total, connect, connectStart, read, readStart, headLen, readLen;

    public StatisticsListener(InternalResponse internalResponse, StatisticsInfo statisticsInfo) {
        this.internalResponse = internalResponse;
        this.statisticsInfo = statisticsInfo;
    }

    public void onStart(AbstractRequest request) {
        total = SystemClock.uptimeMillis();
    }

    public void onEnd(Response res) {
        if (total > 0) {
            total = SystemClock.uptimeMillis() - total;
            internalResponse.setUseTime(total);
            statisticsInfo.addConnectTime(total);

            headLen = internalResponse.getContentLength();
            readLen = internalResponse.getReadedLength();
            long len = 0;
            if (readLen > 0) {
                len = readLen;
            }
            if (len == 0 && headLen > 0) {
                len = headLen;
            }
            statisticsInfo.addDataLength(len);
        }
    }

    public void onRetry(AbstractRequest req, int max, int now) {

    }

    public void onRedirect(AbstractRequest req) {

    }

    public void onPreConnect(AbstractRequest request) {
        connectStart = SystemClock.uptimeMillis();
    }

    public void onAfterConnect(AbstractRequest request) {
        connect += SystemClock.uptimeMillis() - connectStart;
    }

    public void onPreRead(AbstractRequest request) {
        readStart = SystemClock.uptimeMillis();
    }

    public void onAfterRead(AbstractRequest request) {
        read += SystemClock.uptimeMillis() - readStart;
    }

    @Override
    public String toString() {
        return resToString();
    }

    public String resToString() {
        return
                "\n[length]   headerLen: " + headLen
                + " B,    readedLen: " + readLen + " B,    global total len: "
                + statisticsInfo.getDataLength() + " B"
                + "\n[time]   connect  : " + connect + " MS,    read: "
                + read + " MS,    total: " + total + " MS,    global total time: "
                + statisticsInfo.getConnectTime() + " MS";
    }
}