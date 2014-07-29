package com.litesuits.http.data;

import org.apache.http.protocol.HTTP;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class Consts {
    public static final String CODE_CHARSET         = HTTP.UTF_8;
    public static final String EQUALS               = "=";
    public static final String AND      = "&";
    public static final String NONE_SPLIT           = "";
    public static final String ONE_LEVEL_SPLIT      = AND;
    public static final String SECOND_LEVEL_SPLIT   = ",";
    public static final String ARRAY_ECLOSING_LEFT  = "[";
    public static final String ARRAY_ECLOSING_RIGHT = "]";
    public static final String KV_ECLOSING_LEFT     = "{";
    public static final String KV_ECLOSING_RIGHT    = "}";

    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ENCODING_GZIP          = "gzip";
    public static final String REDIRECT_LOCATION      = "location";

    public static final  String DEFAULT_CHARSET      = Charsets.UTF_8;
    public static final  String MIME_TYPE_TEXT       = "text/plain";
    public static final  String MIME_TYPE_JSON       = "application/json";
    public static final  String MIME_TYPE_OCTET_STREAM       = "application/octet-stream";
    public static final  String MIME_TYPE_FORM_URLENCODE      = "application/x-www-form-urlencoded";

    public static final  String DEFAULT_CONTENT_TYPE = MIME_TYPE_TEXT + HTTP.CHARSET_PARAM + DEFAULT_CHARSET;
}
