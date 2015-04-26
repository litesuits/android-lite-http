package com.litesuits.http.parser.impl;

import com.litesuits.http.parser.MemeoryDataParser;
import com.litesuits.http.request.AbstractRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to string.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class StringParser extends MemeoryDataParser<String> {

    public StringParser(AbstractRequest<String> request) {
        super(request);
    }

    @Override
    public String parseNetStream(InputStream stream, long len, String charSet, String cacheDir) throws IOException {
        String data = streamToString(stream, len, charSet);
        if (request.needCache()) {
            keepToCache(data, getSpecifyFile(cacheDir));
        }
        return data;
    }

    @Override
    protected String parseDiskCache(InputStream is, long length) throws IOException {
        return streamToString(is, length, charSet);
    }
}
