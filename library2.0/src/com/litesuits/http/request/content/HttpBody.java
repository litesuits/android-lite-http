package com.litesuits.http.request.content;

import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public abstract class HttpBody {
    protected HttpListener httpListener;
    protected AbstractRequest request;
    public String contentType;

    public String getContentType() {
        return contentType;
    }

    public HttpListener getHttpListener() {
        return httpListener;
    }

    public void setHttpListener(HttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public AbstractRequest getRequest() {
        return request;
    }

    public void setRequest(AbstractRequest request) {
        this.request = request;
        setHttpListener(request.getHttpListener());
    }
}