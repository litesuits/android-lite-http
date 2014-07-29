package com.litesuits.http.request.content.multi;

import com.litesuits.http.data.Consts;

import java.io.UnsupportedEncodingException;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class StringPart extends BytesPart {

    public StringPart(String key, String string) {
        this(key, string, Consts.DEFAULT_CHARSET, Consts.MIME_TYPE_TEXT);
    }

    public StringPart(String key, String string, String charset, String contentType) {
        super(key, getBytes(string, charset), contentType == null ? Consts.MIME_TYPE_TEXT : Consts.MIME_TYPE_TEXT);
    }

    public static byte[] getBytes(String string, String charset) {
        try {
            return string.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_8BIT;
    }
}
