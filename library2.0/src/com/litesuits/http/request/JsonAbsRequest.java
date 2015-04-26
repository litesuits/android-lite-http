package com.litesuits.http.request;

import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.JsonParser;
import com.litesuits.http.request.param.RequestModel;

import java.lang.reflect.ParameterizedType;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public abstract class JsonAbsRequest<T> extends AbstractRequest<T> {

    public JsonAbsRequest() {
        super();
    }

    public JsonAbsRequest(String url) {
        super(url);
    }

    protected JsonAbsRequest(RequestModel model) {
        super(model);
    }

    @Override
    protected DataParser<T> createDataParser() {
        Class claxx = (Class) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return new JsonParser<T>(this, claxx);
    }

}
