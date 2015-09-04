package com.litesuits.http.helper;

import com.litesuits.http.parser.MemCacheableParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class CustomJSONParser extends MemCacheableParser<JSONObject> {

    @Override
    protected JSONObject parseNetStream(InputStream stream, long totalLength, String charSet,
                                        String cacheDir) throws IOException {
        return streamToJson(stream, totalLength, charSet);
    }

    @Override
    protected JSONObject parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToJson(stream, length, charSet);
    }

    protected JSONObject streamToJson(InputStream is, long length, String charSet) throws IOException {
        String json = streamToString(is, length, charSet);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}