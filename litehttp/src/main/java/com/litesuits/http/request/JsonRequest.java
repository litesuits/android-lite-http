package com.litesuits.http.request;

import com.litesuits.http.data.TypeToken;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.JsonParser;
import com.litesuits.http.request.param.HttpParamModel;

import java.lang.reflect.Type;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class JsonRequest<T> extends JsonAbsRequest<T> {

    public JsonRequest(String url, Type resultType) {
        super(url);
        setResultType(resultType);
    }

    public JsonRequest(HttpParamModel model, Type resultType) {
        super(model);
        setResultType(resultType);
    }

    public JsonRequest(String url, TypeToken<T> resultType) {
        super(url);
        setResultType(resultType.getType());
    }

    public JsonRequest(HttpParamModel model, TypeToken<T> resultType) {
        super(model);
        setResultType(resultType.getType());
    }

    @Override
    public DataParser<T> createDataParser() {
        return new JsonParser<T>(resultType);
    }
}
