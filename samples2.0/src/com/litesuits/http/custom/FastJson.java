package com.litesuits.http.custom;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.litesuits.http.data.Json;

import java.lang.reflect.Type;

public class FastJson extends Json {
    private static final String TAG = FastJson.class.getSimpleName();

    @Override
    public String toJson(Object src) {
        Log.i(TAG, "FastJson parse object to json string");
        return JSON.toJSONString(src);
    }

    @Override
    public <T> T toObject(String json, Class<T> claxx) {
        Log.i(TAG, "FastJson parse json string to Object");
        return JSON.parseObject(json, claxx);
    }

    @Override
    public <T> T toObject(String s, Type type) {
        return JSON.parseObject(s, type);
    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> claxx) {
        Log.i(TAG, "FastJson parse bytes to Object");
        return JSON.parseObject(bytes, claxx);
    }
}