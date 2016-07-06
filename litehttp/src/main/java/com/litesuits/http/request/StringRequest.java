package com.litesuits.http.request;

import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.StringParser;
import com.litesuits.http.request.param.HttpParamModel;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class StringRequest extends AbstractRequest<String> {

    public StringRequest(String url) {
        super(url);
    }

    public StringRequest(HttpParamModel model) {
        super(model);
    }

    public StringRequest(String url, HttpParamModel model) {
        super(url, model);
    }

    @Override
    public DataParser<String> createDataParser() {
        return new StringParser();
    }

}
