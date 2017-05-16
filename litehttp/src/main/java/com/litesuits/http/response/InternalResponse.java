package com.litesuits.http.response;

import android.util.Log;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.StatisticsListener;
import com.litesuits.http.request.AbstractRequest;

import java.util.ArrayList;

/**
 * Inner Facade {@link InternalResponse } gives {@link com.litesuits.http.LiteHttp}
 * feature-rich
 * capabiblities that set request and response info easy.
 *
 * @author MaTianyu
 *         2014-1-1下午10:00:42
 */
public class InternalResponse<T> implements Response<T> {
    private static final String TAG = InternalResponse.class.getSimpleName();
    protected String charSet = Consts.DEFAULT_CHARSET;
    protected HttpStatus httpStatus;
    protected int retryTimes;
    protected int redirectTimes;
    protected long readedLength;
    protected long contentLength;
    protected String contentEncoding;
    protected String contentType;
    protected long useTime;
    protected ArrayList<NameValuePair> headers;
    protected AbstractRequest<T> request;
    protected StatisticsListener statistics;
    protected HttpException exception;
    protected boolean isCacheHit;
    protected Object tag;

    public InternalResponse(AbstractRequest<T> request) {
        this.request = request;
    }

    public T getResult() {
        return request.getDataParser().getData();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends AbstractRequest<T>> R getRequest() {
        return (R) request;
    }

    public <R extends AbstractRequest<T>> void setRequest(R request) {
        this.request = request;
    }


    @Override
    public boolean isCacheHit() {
        return isCacheHit;
    }

    @Override
    public String getRawString() {
        return request.getDataParser().getRawString();
    }

    @Override
    public Response<T> setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    public void setCacheHit(boolean isCacheHit) {
        this.isCacheHit = isCacheHit;
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
        if (charSet != null) {
            this.charSet = charSet;
        }
    }

    @Override
    public ArrayList<NameValuePair> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<NameValuePair> headers) {
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
    public String getContentEncoding() {
        return contentEncoding;
    }

    public InternalResponse setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public InternalResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public StatisticsListener getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsListener listener) {
        this.statistics = listener;
    }

    @Override
    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Override
    public int getRedirectTimes() {
        return redirectTimes;
    }

    public void setRedirectTimes(int redirectTimes) {
        this.redirectTimes = redirectTimes;
    }

    @Override
    public long getReadedLength() {
        return readedLength;
    }

    public void setReadedLength(long readedLength) {
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
    public boolean isConnectSuccess() {
        return httpStatus != null && httpStatus.isSuccess();
    }

    public boolean isResultOk() {
        return getResult() != null;
    }

    @Override
    public String toString() {
        return resToString();
    }

    @Override
    public String resToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("^_^\n")
          .append("____________________________ lite http response info start ____________________________")
          .append("\n url            : ").append(request.getUri())
          .append("\n status         : ").append(httpStatus)
          .append("\n cache hit      : ").append(isCacheHit)
          .append("\n charSet        : ").append(charSet)
          .append("\n useTime        : ").append(useTime)
          .append("\n retryTimes     : ").append(retryTimes)
          .append("\n redirectTimes  : ").append(redirectTimes)
          .append("\n readedLength   : ").append(readedLength)
          .append("\n contentLength  : ").append(contentLength)
          .append("\n contentEncoding: ").append(contentEncoding)
          .append("\n contentType    : ").append(contentType)
          .append("\n statistics     : ").append(statistics)
          .append("\n tag            : ").append(tag)
          .append("\n header         ");
        if (headers == null) {
            sb.append(": null");
        } else {
            for (NameValuePair nv : headers) {
                sb.append("\n|    ").append(nv);
            }
        }
        sb.append("\n ").append(request)
          .append("\n exception      : ").append(exception)
          .append("\n.")
          .append("\n _________________ data-start _________________")
          .append("\n ").append(getResult())
          .append("\n _________________ data-over _________________")
          .append("\n.")
          .append("\n model raw string     : ").append(getRawString())
          .append("\n____________________________ lite http response info end ____________________________");
        return sb.toString();
    }

    @Override
    public void printInfo() {
        Log.i(TAG, resToString());
    }
}
