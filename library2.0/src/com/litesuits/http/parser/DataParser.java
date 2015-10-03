package com.litesuits.http.parser;

import com.litesuits.http.HttpConfig;
import com.litesuits.http.data.Consts;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * base data parser
 *
 * @author MaTianyu
 *         2014-2-21下午7:26:58
 */
public abstract class DataParser<T> {
    protected static final String TAG = DataParser.class.getSimpleName();
    protected T data;
    protected long readLength;
    protected AbstractRequest<T> request;
    protected String charSet = Consts.DEFAULT_CHARSET;
    protected int buffSize = HttpConfig.DEFAULT_BUFFER_SIZE;

    /**
     * source 1. read data form network
     */
    public T readFromNetStream(InputStream stream, long len,
                               String charSet) throws IOException {
        if (stream != null) {
            try {
                this.data = parseNetStream(stream, len, charSet);
            } finally {
                stream.close();
            }
        }
        return this.data;
    }

    /**
     * source 2. read data form disk
     */
    public abstract T readFromDiskCache(File file) throws IOException;


    /**
     * source 3. read data form network
     */
    public final T readFromMemoryCache(T data) {
        if (isMemCacheSupport()) {
            this.data = data;
        }
        return this.data;
    }

    /**
     * parse network stream
     */
    protected abstract T parseNetStream(InputStream stream, long totalLength,
                                        String charSet) throws IOException;

    /**
     * is memory cache supported
     */
    public abstract boolean isMemCacheSupport();

    /**
     * get the data
     *
     * @return custom data
     */
    public final T getData() {
        return data;
    }


    /**
     * length of data
     */
    public final long getReadedLength() {
        return readLength;
    }

    /**
     * set a request to parser
     */
    public final void setRequest(AbstractRequest<T> request) {
        this.request = request;
        if (request.getCharSet() != null) {
            charSet = request.getCharSet();
        }
    }

    public String getRawString() {
        return null;
    }

    /**
     * translate original bytes to custom bytes.
     * if your data is encrypted, you can override this method to decrypt it.
     *
     * @param bytes data form server
     * @return decrypt data
     */
    protected byte[] translateBytes(byte[] bytes) {
        return bytes;
    }

    /**
     * notify readed length to listener
     */
    protected final void notifyReading(long total, long len) {
        HttpListener<T> listener = request.getHttpListener();
        if (listener != null) {
            listener.notifyCallLoading(request, total, len);
        }
    }

    @Override
    public String toString() {
        return "DataParser{" +
               "buffSize=" + buffSize +
               ", readLength=" + readLength +
               '}';
    }
}
