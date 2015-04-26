package com.litesuits.http.parser.impl;

import com.litesuits.http.data.Json;
import com.litesuits.http.parser.MemeoryDataParser;
import com.litesuits.http.request.AbstractRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to string.
 *
 * @author MaTianyu
 *         2014-4-19
 */
public class JsonParser<T> extends MemeoryDataParser<T> {
    private Class<T> claxx;
    private String json;

    public JsonParser(Class<T> claxx) {
        this(null, claxx);
    }

    public JsonParser(AbstractRequest<T> request, Class<T> claxx) {
        super(request);
        this.claxx = claxx;
    }

    @Override
    protected T parseNetStream(InputStream stream, long totalLength, String charSet, String cacheDir) throws IOException {
        json = streamToString(stream, totalLength, charSet);
        if(request.needCache()){
            keepToCache(json,getSpecifyFile(cacheDir));
        }
        return Json.get().toObject(json, claxx);
    }

    @Override
    protected T parseDiskCache(InputStream is, long length) throws IOException {
        json = streamToString(is, length, charSet);
        return Json.get().toObject(json, claxx);
    }

    @Override
    public String toString() {
        return "JsonParser{" +
               "claxx=" + claxx +
               "} " + super.toString();
    }
}
