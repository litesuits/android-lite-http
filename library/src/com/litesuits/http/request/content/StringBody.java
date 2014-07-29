package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;
import org.apache.http.protocol.HTTP;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class StringBody extends AbstractContentBody {
    public String string;
    public String charset;

    public StringBody(String string) {
        this(string, null, null);
    }

    public StringBody(String string, String mimeType, String charset) {
        this.string = string;
        if (mimeType == null) {
            mimeType = Consts.MIME_TYPE_TEXT;
        }
        if (charset == null) {
            charset = Consts.DEFAULT_CHARSET;
        }
        this.charset = charset;
        this.contentType = mimeType + HTTP.CHARSET_PARAM + charset;
    }

    @Override
    public String toString() {
        return "StringEntity{" +
                "string='" + string + '\'' +
                ", charset='" + charset + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
