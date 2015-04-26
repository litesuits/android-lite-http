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
public interface RequestRichModel<T> extends RequestModel {
    LinkedHashMap<String, String> createHeaders();

    ModelQueryBuilder createQueryBuilder();

    HttpListener<T> createHttpListener();

    HttpBody createHttpBody();
}
