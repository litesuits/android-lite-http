package com.litesuits.http.parser;

import org.apache.http.util.CharArrayBuffer;

import java.io.*;

/**
 * parse inputstream to string.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class StringParser extends DataParser<String> {

    @Override
    public String parseData(InputStream stream, long len, String charSet) throws IOException {
        return streamToString(stream, len, charSet);
    }

    private String streamToString(InputStream is, long len, String charSet) throws IOException {
        if (len > 0) {
            Reader reader = new InputStreamReader(is, charSet);
            CharArrayBuffer buffer = new CharArrayBuffer((int) len);
            try {
                char[] tmp = new char[buffSize];
                int l;
                while (!Thread.currentThread().isInterrupted() && (l = reader.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                    readLength += l;
                }
            } finally {
                reader.close();
            }
            return buffer.toString();
        } else {
//            ByteArrayOutputStream baos = null;
//            try {
//                 baos = new ByteArrayOutputStream(1024);
//                byte[] buffer = new byte[1024];
//                int l = 0;
//                while ((l = is.read(buffer)) != -1) {
//                    baos.write(buffer, 0, l);
//                }
//                return baos.toString();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if(baos != null) baos.close();
//            }
//            return null;
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            try {
                byte[] buff = new byte[buffSize];
                int l = 0;
                while (!Thread.currentThread().isInterrupted() && (l = is.read(buff)) > 0) {
                    swapStream.write(buff, 0, l);
                    readLength += l;
                }
                return swapStream.toString(charSet);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                swapStream.close();
            }
            return null;
        }

    }


}
