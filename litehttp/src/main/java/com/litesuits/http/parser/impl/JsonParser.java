package com.litesuits.http.parser.impl;

import com.litesuits.http.data.Json;
import com.litesuits.http.parser.MemCacheableParser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * parse inputstream to java model.
 *
 * @author MaTianyu
 *         2014-4-19
 */
public class JsonParser<T> extends MemCacheableParser<T> {
    protected Type claxx;
    protected String json;

    public JsonParser(Type claxx) {
        this.claxx = claxx;
    }

    @Override
    protected T parseNetStream(InputStream stream, long totalLength
            , String charSet) throws IOException {
        json = streamToString(stream, totalLength, charSet);
        return Json.get().toObject(json, claxx);
    }

    @Override
    protected T parseDiskCache(InputStream stream, long length) throws IOException {
        json = streamToString(stream, length, charSet);
        return Json.get().toObject(json, claxx);
    }

    @Override
    protected boolean tryKeepToCache(T data) throws IOException {
        return keepToCache(json);
    }

    /**
     * get the row string
     */
    @Override
    public String getRawString() {
        return json;
    }

    /**
     * get the json model
     */
    public <C> C getJsonModel(Class<C> claxx) {
        return Json.get().toObject(json, claxx);
    }

    @Override
    public String toString() {
        return "JsonParser{" +
               "claxx=" + claxx +
               "} " + super.toString();
    }
}
