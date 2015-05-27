package com.litesuits.http.parser.impl;

import com.litesuits.http.parser.MemeoryDataParser;
import com.litesuits.http.request.AbstractRequest;
import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * parse inputstream to bytes.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class BytesParser extends MemeoryDataParser<byte[]> {

    public BytesParser(AbstractRequest<byte[]> request) {
        super(request);
    }

    @Override
    public byte[] parseNetStream(InputStream stream, long len, String charSet, String cacheDir) throws IOException {
        byte[] data = streamToByteArray(stream, len);
        if(request.needCache()){
            keepToCache(data,getSpecifyFile(cacheDir));
        }
        return data;
    }

    @Override
    protected byte[] parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToByteArray(stream, length);
    }

}
