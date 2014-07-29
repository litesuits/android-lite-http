package com.litesuits.http.request.content;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class ByteArrayBody extends AbstractContentBody {
    public byte[] bytes;

    public ByteArrayBody(byte[] bytes) {
        this(bytes, null);
    }

    public ByteArrayBody(byte[] bytes, String contentType) {
        this.bytes = bytes;
        this.contentType = contentType;
    }
}
