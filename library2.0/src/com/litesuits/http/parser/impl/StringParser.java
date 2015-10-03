package com.litesuits.http.parser.impl;

import com.litesuits.http.parser.MemCacheableParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to string.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class StringParser extends MemCacheableParser<String> {

    @Override
    public String parseNetStream(InputStream stream, long len, String charSet) throws IOException {
        return streamToString(stream, len, charSet);
    }

    @Override
    protected String parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToString(stream, length, charSet);
    }

    @Override
    protected boolean tryKeepToCache(String data) throws IOException {
        return keepToCache(data);
    }
}
