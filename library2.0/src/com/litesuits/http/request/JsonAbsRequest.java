package com.litesuits.http.request;

import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.JsonParser;
import com.litesuits.http.request.param.HttpParamModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public abstract class JsonAbsRequest<T> extends AbstractRequest<T> {

    protected Type resultType;

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
    public DataParser<T> createDataParser() {
        return new JsonParser<T>(getResultType());
    }

    public Type getResultType() {
        if (resultType == null) {
            resultType = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return resultType;
    }

    @SuppressWarnings("unchecked")
    public <R extends JsonAbsRequest> R setResultType(Type resultType) {
        this.resultType = resultType;
        return (R) this;
    }
}
