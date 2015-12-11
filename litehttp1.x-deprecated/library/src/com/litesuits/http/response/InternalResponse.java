package com.litesuits.http.response;

import android.graphics.Bitmap;
import com.litesuits.android.log.Log;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.Json;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpInnerListener;
import com.litesuits.http.parser.*;
import com.litesuits.http.request.Request;

import java.io.File;
import java.io.InputStream;

/**
 * Inner Facade {@link InternalResponse } gives {@link LiteHttpClient}
 * feature-rich
 * capabiblities that set request and response info easy.
 *
 * @author MaTianyu
 *         2014-1-1下午10:00:42
 */
public class InternalResponse implements Response {
    private static final String TAG = InternalResponse.class.getSimpleName();
    protected HttpStatus httpStatus;
    protected String charSet = Consts.DEFAULT_CHARSET;
    protected int               tryTimes;
    protected int               redirectTimes;
    /**
     * data real size
     */
    protected int               readedLength;
    /**
     * http header Content-Length
     */
    protected long              contentLength;
    protected long              useTime;
    protected NameValuePair[]   headers;
    protected Request           request;
    protected DataParser<?>     dataParser;
    protected HttpInnerListener httpInnerListener;
    protected HttpException     exception;

    @Override
    public String getString() {
        if (dataParser instanceof StringParser) {
            return ((StringParser) dataParser).getData();
        } else {
            throw new RuntimeException("get string , your Request must use StringParser");
        }
    }

    @Override
    public byte[] getBytes() {
        if (dataParser instanceof BinaryParser) {
            return ((BinaryParser) dataParser).getData();
        } else {
            throw new RuntimeException("get bytes , your Request must use BinaryParser");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getObject(Class<T> claxx) {
        if (dataParser instanceof BinaryParser) {
            try {
                if (claxx == String.class) return (T) new String(((BinaryParser) dataParser).getData(), getCharSet());
                return Json.get().toObject(((BinaryParser) dataParser).getData(), claxx);
            } catch (Exception e) {
                e.printStackTrace();
                if (exception == null) setException(new HttpClientException(e));
            }
        } else if (dataParser instanceof StringParser) {
            if (claxx == String.class) return (T) dataParser.getData();
            try {
                return Json.get().toObject(((StringParser) dataParser).getData(), claxx);
            } catch (Exception e) {
                e.printStackTrace();
                if (exception == null) setException(new HttpClientException(e));
            }
        } else {
            throw new RuntimeException("Json To Java Object , your Request must use BinaryParser or StringParser");
        }
        return null;
    }

    @Override
    public <T> T getObjectWithMockData(Class<T> claxx, String json) {
        try {
            return Json.get().toObject(json, claxx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Json To Java Object , your Request must use BinaryParser or StringParser");
    }

    @Override
    public File getFile() {
        if (dataParser instanceof FileParser) {
            return ((FileParser) dataParser).getData();
        } else {
            throw new RuntimeException("get file , your Request must use FileParser");
        }
    }

    @Override
    public Bitmap getBitmap() {
        if (dataParser instanceof BitmapParser) {
            return ((BitmapParser) dataParser).getData();
        } else {
            throw new RuntimeException("get bytes , your Request must use BitmapParser");
        }
    }

    /**
     * @deprecated
     */
    @Override
    public InputStream getInputStream() {
        if (dataParser instanceof BinaryParser) {
            return ((InputStreamParser) dataParser).getData();
        } else {
            throw new RuntimeException("get InputStream ,your Request must use InputStreamParser");
        }
    }

    @Override
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public HttpException getException() {
        return exception;
    }

    public void setException(HttpException e) {
        this.exception = e;
    }

    @Override
    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        if (charSet != null) this.charSet = charSet;
    }

    @Override
    public NameValuePair[] getHeaders() {
        return headers;
    }

    public void setHeaders(NameValuePair[] headers) {
        this.headers = headers;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    public long setContentLength(long contentLength) {
        this.contentLength = contentLength;
        return this.contentLength;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpInnerListener getHttpInnerListener() {
        return httpInnerListener;
    }

    public void setHttpInnerListener(HttpInnerListener listener) {
        this.httpInnerListener = listener;
    }

    @Override
    public int getTryTimes() {
        return tryTimes;
    }

    public void setTryTimes(int retryTimes) {
        this.tryTimes = retryTimes;
    }

    @Override
    public int getRedirectTimes() {
        return redirectTimes;
    }

    public void setRedirectTimes(int redirectTimes) {
        this.redirectTimes = redirectTimes;
    }

    @Override
    public int getReadedLength() {
        return readedLength;
    }

    public void setReadedLength(int readedLength) {
        this.readedLength = readedLength;
    }

    @Override
    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    @Override
    public DataParser<?> getDataParser() {
        return dataParser;
    }

    public void setDataParser(DataParser<?> dataParser) {
        this.dataParser = dataParser;
    }

    @Override
    public boolean isSuccess() {
        return httpStatus != null && httpStatus.isSuccess();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~-- Http Response Info Start --~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                .append("\n  ").append(httpStatus)
                .append("\n  charSet = ").append(charSet)
                .append("\n  useTime = ").append(useTime)
                .append("\n  tryTimes = ").append(tryTimes).append(", redirectTimes = ").append(redirectTimes)
                .append("\n  readedLength = ").append(readedLength).append(", contentLength=").append(contentLength)
                .append("\n  -----requst----- ").append(request)
                .append("\n  -----requst----- ")
                .append("\n  -----header----- ");
        if (headers == null) {
            sb.append("\n  null ");
        } else {
            for (NameValuePair nv : headers) {
                sb.append("\n  ").append(nv);
            }
        }
        sb.append("\n  -----header----- ")
                .append("\n  -----data----- ")
                .append("\n  ").append(dataParser != null ? dataParser.getData() : "null")
                .append("\n  -----data----- ")
                .append("\n  exception : ").append(exception)
                .append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~-- Http Response Info End --~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (exception != null) Log.e(this.getClass().getSimpleName(), "exception: " + exception);
        return sb.toString();

    }

    @Override
    public void printInfo() {
        if (!Log.isPrint) return;
        String msg = "";
        msg += "\n\t ";
        try {
            msg += "\n\turl = " + request.getUrl();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
        msg += "\n\t" + httpStatus;
        msg += "\n\tcharSet = " + charSet;
        msg += "\n\tuseTime = " + useTime;
        msg += "\n\ttryTimes = " + tryTimes + ", redirectTimes = " + redirectTimes;
        msg += "\n\treadedLength = " + readedLength + ", contentLength=" + contentLength;
        Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~-- Http Response Info Start --~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Log.d(TAG, msg);

        //request
        Log.i(TAG, "  -----http requst----- ↓ ");
        Log.d(TAG, request);

        //header
        msg = "";
        if (headers == null) {
            msg = "null";
        } else {
            msg += "\n\t ";
            for (NameValuePair nv : headers) {
                msg += "\n\t" + nv;
            }
        }
        Log.i(TAG, "  -----reponse header----- ↓");
        Log.d(TAG, msg);

        //data
        msg = "\t" + dataParser != null ? "" + dataParser.getData() : "null";
        Log.i(TAG, "  -----reponse data----- ↓ \n");
        Log.d(TAG, msg);

        //exception
        if (exception != null) {
            Log.w(TAG, "  -----异常(Exception)----- ↓ ");
            Log.w(TAG, exception);
        }
        Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~-- Http Response Info End --~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    //    @Override
    //    public String toString() {
    //        StringBuilder sb = new StringBuilder();
    //        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~-- Http Response Info Start --~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    //                .append("\n  ").append(httpStatus)
    //                .append("\n  charSet = ").append(charSet)
    //                .append("\n  useTime = ").append(useTime)
    //                .append("\n  tryTimes = ").append(tryTimes).append(", redirectTimes = ").append(redirectTimes)
    //                .append("\n  readedLength = ").append(readedLength).append(", contentLength=").append(contentLength)
    //                .append("\n  -----header----- ");
    //        if (headers == null) {
    //            sb.append("\n  null ");
    //        } else {
    //            for (NameValuePair nv : headers) {
    //                sb.append("\n  ").append(nv);
    //            }
    //        }
    //        sb.append("\n  -----header----- ")
    //                .append("\n  -----requst----- ").append(request)
    //                .append("\n  -----requst----- ")
    //                .append("\n  -----data----- ")
    //                .append("\n  ").append(dataParser != null ? dataParser.getData() : "null")
    //                .append("\n  -----data----- ")
    //                .append("\n  exception : ").append(exception)
    //                .append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~-- Http Response Info End --~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    //        if (exception != null) Log.e(this.getClass().getSimpleName(), "exception: " + exception);
    //        return sb.toString();
    //
    //    }
}
