package com.litesuits.http.response;

import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.AbstractRequest;

import java.util.ArrayList;

/**
 * User Facade
 * providing developers with easy access to the results of
 * {@link com.litesuits.http.LiteHttp#execute(AbstractRequest)},
 * and with information of status,request,charset,etc.
 *
 * @author MaTianyu
 *         2014-1-1下午10:00:42
 */
public interface Response<T> {


    ArrayList<NameValuePair> getHeaders();

    HttpStatus getHttpStatus();

    T getResult();

    <R extends AbstractRequest<T>> R getRequest();

    long getReadedLength();

    long getContentLength();


    String getContentEncoding();

    String getContentType();

    String getCharSet();

    long getUseTime();

    boolean isConnectSuccess();

    int getRetryTimes();

    int getRedirectTimes();

    HttpException getException();

    boolean isCacheHit();

    String getRawString();

    Response<T> setTag(Object tag);

    Object getTag();

    String resToString();

    void printInfo();

}
