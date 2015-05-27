package com.litesuits.http.request;

import com.litesuits.http.parser.impl.JsonAbsParser;
import com.litesuits.http.request.param.HttpParamModel;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public abstract class JsonAbsRequest<T> extends AbstractRequest<T> {
    protected JsonAbsParser<T> jsonParser;

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
    public JsonAbsParser<T> getDataParser() {
        if (jsonParser == null) {
            jsonParser = new JsonAbsParser<T>(this) {};
        }
        return jsonParser;
    }
}
