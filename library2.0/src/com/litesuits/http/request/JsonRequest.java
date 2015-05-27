package com.litesuits.http.request;

import com.litesuits.http.data.TypeToken;
import com.litesuits.http.parser.impl.JsonParser;
import com.litesuits.http.request.param.HttpParamModel;

import java.lang.reflect.Type;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class JsonRequest<T> extends AbstractRequest<T> {

    private Type resultType;

    protected JsonParser<T> jsonParser;

    public JsonRequest(String url, Type resultType) {
        super(url);
        this.resultType = resultType;
    }

    public JsonRequest(HttpParamModel model, Type resultType) {
        super(model);
        this.resultType = resultType;
    }
    public JsonRequest(String url, TypeToken<T> resultType) {
        super(url);
        this.resultType = resultType.getType();
    }

    public JsonRequest(HttpParamModel model, TypeToken<T> resultType) {
        super(model);
        this.resultType = resultType.getType();
    }

    @Override
    public JsonParser<T> getDataParser() {
        if (jsonParser == null) {
            jsonParser = new JsonParser<T>(this, resultType);
        }
        return jsonParser;
    }
}
