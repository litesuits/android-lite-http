package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class ByteArrayBody extends HttpBody {
    private byte[] bytes;

    public ByteArrayBody(byte[] bytes) {
        this(bytes, Consts.MIME_TYPE_OCTET_STREAM);
    }

    public ByteArrayBody(byte[] bytes, String contentType) {
        this.bytes = bytes;
        this.contentType = contentType;
    }


    @Override
    public long getContentLength() {
        return bytes.length;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        outstream.write(bytes);
        outstream.flush();
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return "ByteArrayBody{" +
               "bytes=" + Arrays.toString(bytes) +
               "} " + super.toString();
    }
}
