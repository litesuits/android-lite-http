package com.litesuits.http.listener;

import com.litesuits.http.request.Request;

/**
 * @author MaTianyu
 * @date 2014-11-06
 */
public interface HttpConnectListener {
    public void onPreConnect(Request req);
    public void onAfterConnect(Request req);
}
