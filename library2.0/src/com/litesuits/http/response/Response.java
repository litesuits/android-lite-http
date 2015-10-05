package com.litesuits.http.response;

import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.AbstractRequest;

/**
 * User Facade
 * providing developers with easy access to the results of
 * {@link com.litesuits.http.LiteHttp#execute(com.litesuits.http.request.AbstractRequest)},
 * and with information of status,request,charset,etc.
 *
 * @author MaTianyu
 *         2014-1-1下午10:00:42
 */
public interface Response<T> {


    public NameValuePair[] getHeaders();

    public HttpStatus getHttpStatus();

    public T getResult();

    public <R extends AbstractRequest<T>> R getRequest();

    public long getReadedLength();

    public long getContentLength();

    public String getCharSet();

    public long getUseTime();

    public boolean isConnectSuccess();

    public int getRetryTimes();

    public int getRedirectTimes();

    public HttpException getException();

    public boolean isCacheHit();

    public String getRawString();

    public Response<T> setTag(Object tag);

    public Object getTag();

    String resToString();

    void printInfo();

}
