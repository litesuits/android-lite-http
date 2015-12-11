package com.litesuits.http.data;

import org.apache.http.protocol.HTTP;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class Consts {
    public static final String CODE_CHARSET         = HTTP.UTF_8;
    public static final String EQUALS               = "=";
    public static final String AND                  = "&";
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

    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String CONTENT_LEN       = "Content-Length";
    public static final String CONTENT_TYPE      = "Content-Type";
    public static final String CONTENT_ENCODING  = "Content-Encoding";
    public static final String EXPECT_DIRECTIVE  = "Expect";
    public static final String CONN_DIRECTIVE    = "Connection";
    public static final String TARGET_HOST       = "Host";
    public static final String USER_AGENT        = "User-Agent";
    public static final String DATE_HEADER       = "Date";
    public static final String SERVER_HEADER     = "Server";

    public static final String EXPECT_CONTINUE = "100-Continue";
    public static final String CONN_CLOSE      = "Close";
    public static final String CONN_KEEP_ALIVE = "Keep-Alive";

    public static final String DEFAULT_CHARSET          = Charsets.UTF_8;
    public final static String CHARSET_PARAM            = "; charset=";
    public final static String BOUNDARY_PARAM            = "; boundary=";
    public static final String MIME_TYPE_TEXT           = "text/plain";
    public static final String MIME_TYPE_JSON           = "application/json";
    public static final String MIME_TYPE_OCTET_STREAM   = "application/octet-stream";
    public static final String MIME_TYPE_FORM_URLENCODE = "application/x-www-form-urlencoded";
    public static final String MIME_TYPE_FORM_DATA      = "multipart/form-data";

    public static final String DEFAULT_CONTENT_TYPE = MIME_TYPE_TEXT + HTTP.CHARSET_PARAM + DEFAULT_CHARSET;
}
