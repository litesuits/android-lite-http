package com.litesuits.http.custom;

import com.litesuits.http.parser.MemCacheableParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * parse stream to JSONObject
 */
public class CustomJSONParser extends MemCacheableParser<JSONObject> {
    String json;

    /**
     * 实现远程网络流解析
     */
    @Override
    protected JSONObject parseNetStream(InputStream stream, long totalLength,
                                        String charSet) throws IOException {
        return streamToJson(stream, totalLength, charSet);
    }

    /**
     * 实现本地文件流解析
     */
    @Override
    protected JSONObject parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToJson(stream, length, charSet);
    }

    /**
     * 实现文件缓存
     */
    @Override
    protected boolean tryKeepToCache(JSONObject data) throws IOException {
        return keepToCache(json);
    }

    /**
     * 1. 将 stream 转换为 String
     * 2. String 转为 JSONObject
     */
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