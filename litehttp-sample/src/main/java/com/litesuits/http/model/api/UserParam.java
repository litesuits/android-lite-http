package com.litesuits.http.model.api;

import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.request.param.HttpParamModel;
import com.litesuits.http.request.param.NonHttpParam;

/**
 * Param Model: will be converted to: http://...?id=168&key=md5
 */
public class UserParam implements HttpParamModel {
    // static final property will be ignored.
    private static final long serialVersionUID = 2451716801614350437L;

    private String uri;

    @HttpParam("id")
    private long id;

    public String key;

    @NonHttpParam
    protected String ignored = "Ignored by @NonHttpParam ";

    public UserParam(long id, String key) {
        this.id = id;
        this.key = key;
    }
}