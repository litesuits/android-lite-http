package com.litesuits.http.request.param;

import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.content.StringBody;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.request.content.multi.MultipartBody;
import com.litesuits.http.request.query.ModelQueryBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

/**
 * mark a class as a http parameter modle.
 * classes that implement this will be parsed to http parameter.
 * 杂袍1只， 墨兰1对， 大红1对， 红鼻10条，金光10条，玻璃扯旗10条，三角10条，红绿灯10条。
 *
 * @author MaTianyu
 *         2014-1-19上午2:39:31
 */
public abstract class HttpRichParamModel<T> implements HttpParamModel {
    private HttpListener<T> httpListener;

    public final LinkedHashMap<String, String> getHeaders() {
        return createHeaders();
    }

    public ModelQueryBuilder getModelQueryBuilder() {
        return createQueryBuilder();
    }

    public final HttpBody getHttpBody() {
        return createHttpBody();
    }

    public final HttpListener<T> getHttpListener() {
        if (httpListener == null) {
            httpListener = createHttpListener();
        }
        return httpListener;
    }

    public boolean isFieldsAttachToUrl() {
        return true;
    }

    /**
     * craete headers for request.
     */
    protected LinkedHashMap<String, String> createHeaders() {return null;}

    /**
     * craete uri query builder for request.
     */
    protected ModelQueryBuilder createQueryBuilder() {
        return null;
    }

    /**
     * create http body for POST/PUT... request.
     *
     * @return such as {@link StringBody}, {@link UrlEncodedFormBody}, {@link MultipartBody}...
     */
    protected HttpBody createHttpBody() {return null;}

    /**
     * create http listener for request.
     */
    protected HttpListener<T> createHttpListener() {return null;}

    /**
     * build request and set http listener.
     */
    @SuppressWarnings("unchecked")
    public final <M extends HttpRichParamModel<T>> M setHttpListener(HttpListener<T> httpListener) {
        this.httpListener = httpListener;
        return (M) this;
    }

    /**
     * build as a request.
     */
    public JsonRequest<T> buildRequest() {
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return new JsonRequest<T>(this, type);
    }
}
