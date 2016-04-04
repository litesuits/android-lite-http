package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;
import com.litesuits.http.data.Json;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class JsonBody extends StringBody {

    public JsonBody(Object param) {
        this(param, Consts.DEFAULT_CHARSET);
    }

    public JsonBody(String param) {
        this(param, Consts.DEFAULT_CHARSET);
    }

    public JsonBody(String json, String charset) {
        super(json, Consts.MIME_TYPE_JSON, charset);
    }

    public JsonBody(Object param, String charset) {
        super(Json.get().toJson(param), Consts.MIME_TYPE_JSON, charset);
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
