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
    protected byte[] parseDiskCache(InputStream is, long length) throws IOException {
        return streamToByteArray(is, length);
    }

    protected byte[] streamToByteArray(InputStream is, long len) throws IOException {
        if (len > 0) {
            final ByteArrayBuffer buffer = new ByteArrayBuffer((int) len);
            final byte[] tmp = new byte[buffSize];
            int l;
            while (!request.isCancelledOrInterrupted() && (l = is.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
                readLength += l;
            }
            return buffer.toByteArray();
        } else {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            try {
                byte[] buff = new byte[buffSize];
                int l = 0;
                while (!request.isCancelledOrInterrupted() && (l = is.read(buff)) > 0) {
                    swapStream.write(buff, 0, l);
                    readLength += l;
                }
                return swapStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                swapStream.close();
            }
            return null;
        }

    }
}
