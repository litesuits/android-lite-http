package com.litesuits.http.listener;

import com.litesuits.http.request.Request;

/**
 * @author MaTianyu
 * @date 2014-11-06
 */
public interface HttpReadingListener {
    public void onReading(Request req, long total, int len);
}
