package com.litesuits.http.request;

import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.impl.BytesParser;
import com.litesuits.http.request.param.HttpParamModel;

/**
 * @author MaTianyu
 * @date 2015-04-18
 */
public class BytesRequest extends AbstractRequest<byte[]> {

    public BytesRequest(String url) {
        super(url);
    }

    public BytesRequest(HttpParamModel model) {
        super(model);
    }

    @Override
    public DataParser<byte[]> createDataParser() {
        return new BytesParser();
    }

}
