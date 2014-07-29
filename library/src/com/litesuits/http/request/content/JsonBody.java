package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;
import com.litesuits.http.data.Json;
import com.litesuits.http.request.param.HttpParam;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class JsonBody extends StringBody {

    public JsonBody(HttpParam param) {
        this(param, Consts.DEFAULT_CHARSET);
    }

    public JsonBody(HttpParam param, String charset) {
        super(handleString(param), Consts.MIME_TYPE_JSON, charset);
    }

    private static String handleString(HttpParam param) {
        return Json.get().toString(param);
    }

    @Override
    public String toString() {
        return "StringEntity{" +
                "string='" + string + '\'' +
                ", charset='" + charset + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
