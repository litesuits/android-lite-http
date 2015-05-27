package com.litesuits.http.parser.impl;

import com.litesuits.http.request.AbstractRequest;

import java.lang.reflect.Type;

/**
 * parse inputstream to java model.
 *
 * @author MaTianyu
 *         2014-4-19
 */
public class JsonParser<T> extends JsonAbsParser<T> {
    public JsonParser(AbstractRequest<T> request, Type claxx) {
        super(request, claxx);
    }
}
