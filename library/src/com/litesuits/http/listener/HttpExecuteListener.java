package com.litesuits.http.listener;

import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * @author MaTianyu
 * @date 2014-11-06
 */
public interface HttpExecuteListener {
    public void onStart(Request req) throws HttpException;

    public void onEnd(Response res);

    public void onRetry(Request req, int max, int now);

    public void onRedirect(Request req);
}
