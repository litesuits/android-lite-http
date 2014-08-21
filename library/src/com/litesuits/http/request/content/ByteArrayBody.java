package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class ByteArrayBody extends HttpBody {
    public byte[] bytes;

    public ByteArrayBody(byte[] bytes) {
        this(bytes, Consts.MIME_TYPE_OCTET_STREAM);
    }

    public ByteArrayBody(byte[] bytes, String contentType) {
        this.bytes = bytes;
        this.contentType = contentType;
    }
}
