package com.litesuits.http.request;

import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.JsonParser;
import com.litesuits.http.request.param.RequestModel;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class JsonRequest<T> extends AbstractRequest<T> {

    private Class<T> resultType;

    public JsonRequest() {
        super();
    }

    public JsonRequest(String url, Class<T> resultType) {
        super(url);
        this.resultType = resultType;
    }

    public JsonRequest(RequestModel model, Class<T> resultType) {
        super(model);
        this.resultType = resultType;
    }

    @Override
    protected DataParser<T> createDataParser() {
        return new JsonParser<T>(this, resultType);
    }

}
