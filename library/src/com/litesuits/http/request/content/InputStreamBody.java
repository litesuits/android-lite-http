package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;

import java.io.InputStream;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class InputStreamBody extends HttpBody {
    public InputStream inputStream;

    public InputStreamBody(InputStream inputStream) {
        this(inputStream, null);
    }

    public InputStreamBody(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = (contentType != null) ? contentType : Consts.MIME_TYPE_OCTET_STREAM;
    }

    @Override
    public String toString() {
        return "InputStreamEntity{" +
                "inputStream=" + inputStream +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
