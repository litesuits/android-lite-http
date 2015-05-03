package com.litesuits.http.request;

import com.litesuits.http.parser.impl.StringParser;
import com.litesuits.http.request.param.HttpParamModel;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class StringRequest extends AbstractRequest<String> {

    protected StringParser stringParser;

//    public StringRequest() {
//        super();
//    }

    public StringRequest(String url) {
        super(url);
    }

    public StringRequest(HttpParamModel model) {
        super(model);
    }

    @Override
    public StringParser getDataParser() {
        if (stringParser == null) {
            stringParser = new StringParser(this);
        }
        return stringParser;
    }
}
