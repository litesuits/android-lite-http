package com.litesuits.http.listener;

import com.litesuits.http.request.Request;

/**
 * @author MaTianyu
 * @date 2014-11-06
 */
public interface HttpReadListener {
    public void onPreRead(Request req);

    public void onAfterRead(Request req);
}
