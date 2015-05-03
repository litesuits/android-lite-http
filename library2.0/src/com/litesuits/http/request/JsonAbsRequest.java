package com.litesuits.http.request;

import com.litesuits.http.parser.impl.JsonParser;
import com.litesuits.http.request.param.HttpParamModel;

import java.lang.reflect.ParameterizedType;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public abstract class JsonAbsRequest<T> extends AbstractRequest<T> {
    protected JsonParser<T> jsonParser;

//    public JsonAbsRequest() {
//        super();
//    }

    public JsonAbsRequest(String url) {
        super(url);
    }

    protected JsonAbsRequest(HttpParamModel model) {
        super(model);
    }

    protected JsonAbsRequest(String url, HttpParamModel model) {
        super(url, model);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonParser<T> getDataParser() {
        if(jsonParser == null){
            Class<T> claxx = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            jsonParser = new JsonParser<T>(this, claxx);

        }
        return jsonParser;
    }
}
