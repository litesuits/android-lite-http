package com.litesuits.http.request.content;

import com.litesuits.http.HttpConfig;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public abstract class HttpBody {
    public static final int OUTPUT_BUFFER_SIZE = HttpConfig.DEFAULT_BUFFER_SIZE;
    protected HttpListener httpListener;
    protected AbstractRequest request;
    protected String contentType;
    protected String contentEncoding;

    public String getContentType() {
        return contentType;
    }

    public abstract long getContentLength();

    public abstract void writeTo(OutputStream outstream) throws IOException;

    public HttpBody setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public HttpBody setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
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