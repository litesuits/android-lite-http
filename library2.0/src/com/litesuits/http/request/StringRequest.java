package com.litesuits.http.request;

import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.StringParser;
import com.litesuits.http.request.param.RequestModel;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class StringRequest extends AbstractRequest<String> {

    public StringRequest() {
        super();
    }

    public StringRequest(String url) {
        super(url);
    }

    public StringRequest(RequestModel model) {
        super(model);
    }

    @Override
    protected DataParser<String> createDataParser() {
        return new StringParser(this);
    }
}
