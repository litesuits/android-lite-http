package com.litesuits.http.model.api;

import com.litesuits.http.request.param.HttpParamModel;

/**
 * will be converted to: http://...?id=168&key=md5
 */
public class UserParam implements HttpParamModel {
    // static final property will be ignored.
    private static final long serialVersionUID = 2451716801614350437L;
    public long id;
    private String key;

    public UserParam(long id, String key) {
        this.id = id;
        this.key = key;
    }
}