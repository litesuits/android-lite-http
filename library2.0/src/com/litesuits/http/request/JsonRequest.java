package com.litesuits.http.request;

import com.litesuits.http.parser.impl.JsonParser;
import com.litesuits.http.request.param.HttpParamModel;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class JsonRequest<T> extends AbstractRequest<T> {

    private Class<T> resultType;

    protected JsonParser<T> jsonParser;

//    public JsonRequest(Class<T> resultType) {
//        super();
//    }

    public JsonRequest(String url, Class<T> resultType) {
        super(url);
        this.resultType = resultType;
    }

    public JsonRequest(HttpParamModel model, Class<T> resultType) {
        super(model);
        this.resultType = resultType;
    }

    @Override
    public JsonParser<T> getDataParser() {
        if (jsonParser == null) {
            jsonParser = new JsonParser<T>(this, resultType);
        }
        return jsonParser;
    }
}
