package com.litesuits.http.custom;

import com.litesuits.http.parser.MemCacheableParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class CustomJSONParser extends MemCacheableParser<JSONObject> {
    String json;

    @Override
    protected JSONObject parseNetStream(InputStream stream, long totalLength,
                                        String charSet) throws IOException {
        return streamToJson(stream, totalLength, charSet);
    }

    @Override
    protected JSONObject parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToJson(stream, length, charSet);
    }

    @Override
    protected boolean tryKeepToCache(JSONObject data) throws IOException {
        return keepToCache(json);
    }

    protected JSONObject streamToJson(InputStream is, long length, String charSet) throws IOException {
        this.json = streamToString(is, length, charSet);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}