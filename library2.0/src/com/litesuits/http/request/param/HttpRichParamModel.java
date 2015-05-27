package com.litesuits.http.request.param;

import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.query.ModelQueryBuilder;

import java.util.LinkedHashMap;

/**
 * mark a class as a http parameter modle.
 * classes that implement this will be parsed to http parameter.
 *
 * @author MaTianyu
 *         2014-1-19上午2:39:31
 */
public abstract class HttpRichParamModel<T> implements HttpParamModel {
    @NonHttpParam
    protected HttpListener<T> httpListener;
    @NonHttpParam
    protected LinkedHashMap<String, String> headers;
    @NonHttpParam
    protected ModelQueryBuilder modelQueryBuilder;
    @NonHttpParam
    protected HttpBody httpBody;

    public final HttpListener<T> getHttpListener() {
        if(httpListener == null){
            httpListener = createHttpListener();
        }
        return httpListener;
    }

    @SuppressWarnings("unchecked")
    public final <H extends HttpRichParamModel<T>> H setHttpListener(HttpListener<T> httpListener) {
        this.httpListener = httpListener;
        return (H) this;
    }

    public final LinkedHashMap<String, String> getHeaders() {
        if(headers == null){
            headers = createHeaders();
        }
        return headers;
    }

    public final HttpRichParamModel setHeaders(LinkedHashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public final ModelQueryBuilder getModelQueryBuilder() {
        if(modelQueryBuilder == null){
            modelQueryBuilder = createQueryBuilder();
        }
        return modelQueryBuilder;
    }

    public final HttpRichParamModel setModelQueryBuilder(ModelQueryBuilder modelQueryBuilder) {
        this.modelQueryBuilder = modelQueryBuilder;
        return this;
    }

    public final HttpBody getHttpBody() {
        if(httpBody == null){
            httpBody = createHttpBody();
        }
        return httpBody;
    }

    public final HttpRichParamModel setHttpBody(HttpBody httpBody) {
        this.httpBody = httpBody;
        return this;
    }

    protected LinkedHashMap<String, String> createHeaders() {return null;}

    protected ModelQueryBuilder createQueryBuilder() {return null;}

    protected HttpListener<T> createHttpListener() {return null;}

    protected HttpBody createHttpBody() {return null;}
}
