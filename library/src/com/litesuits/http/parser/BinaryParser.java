package com.litesuits.http.parser;

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
public class BinaryParser extends DataParser<byte[]> {

    @Override
    public byte[] parseData(InputStream stream, long len, String charSet) throws IOException {
        return streamToByteArray(stream, len);
    }

    private byte[] streamToByteArray(InputStream is, long len) throws IOException {
        if (len > 0) {
            final ByteArrayBuffer buffer = new ByteArrayBuffer((int) len);
            final byte[] tmp = new byte[buffSize];
            int l;
            while (!Thread.currentThread().isInterrupted() && (l = is.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
                readLength += l;
            }
            return buffer.toByteArray();
        } else {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            try {
                byte[] buff = new byte[buffSize];
                int l = 0;
                while (!Thread.currentThread().isInterrupted() && (l = is.read(buff)) > 0) {
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
