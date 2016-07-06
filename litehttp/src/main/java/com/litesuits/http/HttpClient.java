package com.litesuits.http;

import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.response.InternalResponse;

/**
 * @author 氢一 @http://def.so
 * @date 2016-04-04
 */
public interface HttpClient {
    public void config(HttpConfig config);
    public <T> void connect(AbstractRequest<T> request, InternalResponse response) throws Exception;
}
