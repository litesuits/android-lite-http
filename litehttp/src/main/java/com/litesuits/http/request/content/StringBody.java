package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class StringBody extends HttpBody {
    protected String string;
    protected String mimeType;
    protected String charset;
    protected byte[] content;


    public StringBody(String string) {
        this(string, null, null);
    }

    public StringBody(String string, String mimeType, String charset) {
        if (mimeType == null) {
            mimeType = Consts.MIME_TYPE_TEXT;
        }
        if (charset == null) {
            charset = Consts.DEFAULT_CHARSET;
        }
        this.charset = charset;
        this.string = string;
        try {
            this.content = string.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.contentType = mimeType + Consts.CHARSET_PARAM + charset;
    }

    @Override
    public long getContentLength() {
        return content.length;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        outstream.write(this.content);
        outstream.flush();
    }

    public String getString() {
        return string;
    }

    public String getCharset() {
        return charset;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "StringBody{" +
               "string='" + string + '\'' +
               ", charset='" + charset + '\'' +
               ", content=" + Arrays.toString(content) +
               "} " + super.toString();
    }

}
