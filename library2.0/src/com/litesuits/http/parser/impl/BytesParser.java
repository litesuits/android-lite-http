package com.litesuits.http.parser.impl;

import com.litesuits.http.parser.MemCacheableParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to bytes.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class BytesParser extends MemCacheableParser<byte[]> {

    @Override
    public byte[] parseNetStream(InputStream stream, long len, String charSet) throws IOException {
        return streamToByteArray(stream, len);
    }

    @Override
    protected byte[] parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToByteArray(stream, length);
    }


    @Override
    protected boolean tryKeepToCache(byte[] data) throws IOException {
        return keepToCache(data);
    }

}
