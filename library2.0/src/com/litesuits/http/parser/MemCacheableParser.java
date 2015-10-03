package com.litesuits.http.parser;

import com.litesuits.http.log.HttpLog;
import com.litesuits.http.utils.StringCodingUtils;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

import java.io.*;
import java.nio.charset.Charset;

/**
 * MemCacheableParser: parse inputstream to data ,save to mem and sdcard.
 *
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public abstract class MemCacheableParser<T> extends DataParser<T> {

    public final T readFromNetStream(InputStream stream, long len, String charSet) throws IOException {
        this.data = super.readFromNetStream(stream, len, charSet);
        if (this.data != null && request.isCachedModel()) {
            tryKeepToCache(data);
        }
        return this.data;
    }

    public final T readFromDiskCache(File file) throws IOException {
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(file);
            data = parseDiskCache(fos, file.length());
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return data;
    }

    protected abstract T parseDiskCache(InputStream stream, long length) throws IOException;

    protected abstract boolean tryKeepToCache(T data) throws IOException;

    @Override
    public boolean isMemCacheSupport() {
        return true;
    }

    /**
     * parse input stream to byte array.
     *
     * @param is  input stream
     * @param len total len
     * @return byte data
     */
    protected final byte[] streamToByteArray(InputStream is, long len) throws IOException {
        if (len > 0) {
            final ByteArrayBuffer buffer = new ByteArrayBuffer((int) len);
            final byte[] tmp = new byte[buffSize];
            int l;
            while (!request.isCancelledOrInterrupted() && (l = is.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
                readLength += l;
            }
            return translateBytes(buffer.toByteArray());
        } else {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            try {
                byte[] buff = new byte[buffSize];
                int l = 0;
                while (!request.isCancelledOrInterrupted() && (l = is.read(buff)) > 0) {
                    swapStream.write(buff, 0, l);
                    readLength += l;
                }
                return translateBytes(swapStream.toByteArray());
            } finally {
                swapStream.close();
            }
        }
    }

    /**
     * translate original string to custom string.
     * if your data is encrypted, you can override this method to decrypt it.
     *
     * @param string data form server
     * @return decrypt data
     */
    protected String translateString(String string) {
        return string;
    }

    /**
     * parse input stream to string.
     *
     * @param stream  input stream
     * @param len     total len
     * @param charSet char set
     * @return string data
     */
    protected final String streamToString(InputStream stream, long len, String charSet) throws IOException {
        if (len > 0) {
            Reader reader = new InputStreamReader(stream, charSet);
            CharArrayBuffer buffer = new CharArrayBuffer((int) len);
            try {
                char[] tmp = new char[buffSize];
                int l;
                while (!request.isCancelledOrInterrupted() && (l = reader.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                    readLength += l;
                    if (buffSize < len) {
                        notifyReading(len, readLength);
                    }
                }
            } finally {
                reader.close();
            }
            return translateString(buffer.toString());
        } else {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            try {
                byte[] buff = new byte[buffSize];
                int l = 0;
                while (!request.isCancelledOrInterrupted() && (l = stream.read(buff)) > 0) {
                    swapStream.write(buff, 0, l);
                    readLength += l;
                    notifyReading(len, readLength);
                }
                return translateString(swapStream.toString(charSet));
            } finally {
                swapStream.close();
            }
        }

    }


    protected final boolean keepToCache(String src) throws IOException {
        if (src != null) {
            return keepToCache(StringCodingUtils.getBytes(src, Charset.forName(charSet)));
        }
        return false;
    }

    protected final boolean keepToCache(byte[] data) throws IOException {
        if (data != null) {
            FileOutputStream fos = null;
            try {
                File file = request.getCachedFile();
                File pFile = file.getParentFile();
                if (!pFile.exists()) {
                    boolean mk = pFile.mkdirs();
                    if (HttpLog.isPrint) {
                        HttpLog.i(TAG, "keep cache mkdirs result: " + mk + "  path:" + pFile.getAbsolutePath());
                    }
                }
                fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
                if (HttpLog.isPrint) {
                    HttpLog.v(TAG,
                            "lite http keep disk cache success, "
                            + "   tag: " + request.getTag()
                            + "   url: " + request.getUri()
                            + "   key: " + request.getCacheKey()
                            + "   path: " + file.getAbsolutePath());
                }
                return true;
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
        return false;
    }

    //    protected byte[] getBytes(String src, Charset charSet) {
    //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
    //            try {
    //                return src.getBytes(charSet.name());
    //            } catch (UnsupportedEncodingException e) {
    //                e.printStackTrace();
    //            }
    //            return null;
    //        } else {
    //            return src.getBytes(charSet);
    //        }
    //    }

}
