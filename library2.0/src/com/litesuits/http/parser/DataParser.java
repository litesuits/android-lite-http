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

    public DataParser(AbstractRequest<T> request) {
        this.request = request;
        if (request.getCharSet() != null) {
            charSet = request.getCharSet();
        }
    }

    public final T readFromNetStream(InputStream stream, long len, String charSet, String cacheDir) throws IOException {
        if (stream != null) {
            try {
                data = parseNetStream(stream, len, charSet, cacheDir);
            } finally {
                stream.close();
            }
        }
        return data;
    }

    public final T readFromMemoryCache(T data) {
        if (isMemCacheSupport()) {
            this.data = data;
        }
        return this.data;
    }

    public abstract T readFromDiskCache(File file);

    /**
     * parse network stream
     */
    protected abstract T parseNetStream(InputStream stream, long totalLength, String charSet, String cacheDir)
            throws IOException;

    /**
     * is memory cache supported
     */
    public abstract boolean isMemCacheSupport();

    /**
     * get cache file name, null will save to default path.
     *
     * @return custom data
     */
    public abstract File getSpecifyFile(String dir);

    /**
     * notify readed length to listener
     */
    protected final void notifyReading(long total, long len) {
        HttpListener<T> listener = request.getHttpListener();
        if (listener != null) {
            listener.loading(request, total, len);
        }
    }

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

    @Override
    public String toString() {
        return "DataParser{" +
               "buffSize=" + buffSize +
               ", readLength=" + readLength +
               '}';
    }
}
