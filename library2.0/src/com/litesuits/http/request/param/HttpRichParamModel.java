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
    private static final long serialVersionUID = -5793415337101322956L;

    public LinkedHashMap<String, String> createHeaders() {return null;}

    public ModelQueryBuilder createQueryBuilder() {return null;}

    public HttpListener<T> createHttpListener() {return null;}

    public HttpBody createHttpBody() {return null;}
}
