package com.litesuits.http.request;

import java.util.LinkedHashMap;

import android.content.Context;

import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.impl.apache.ApacheHttpClient;
import com.litesuits.http.request.param.HttpMethod;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.response.Response;

/**
 * used as RequestBuilder.build("http://t.cn").header("keep-alive","true").param("k","v").param(data).getString().
 * help to build a request
 * @author MaTianyu
 * 2014-1-5下午11:10:19
 */
public class RequestBuilder {
	private Request req;
	private LiteHttpClient client;

	public RequestBuilder(Context context, String uri) {
		client = ApacheHttpClient.getInstance(context);
		req = new Request(uri);
	}

	public static RequestBuilder build(Context context, String uri) {
		return new RequestBuilder(context, uri);
	}

	public RequestBuilder method(HttpMethod method) {
		req.setMethod(method);
		return this;
	}

	public RequestBuilder param(HttpParam paramObject) {
		req.setParamModel(paramObject);
		return this;
	}

	public RequestBuilder param(String key, CharSequence value) {
		req.addParam(key, String.valueOf(value));
		return this;
	}

	public RequestBuilder param(String key, Number value) {
		return param(key, String.valueOf(value));
	}

	public RequestBuilder param(String key, boolean value) {
		return param(key, String.valueOf(value));
	}

	public RequestBuilder clearParam() {
		req.setParamModel(null);
		req.getParamMap().clear();
		return this;
	}

	public RequestBuilder param(LinkedHashMap<String, String> paramMap) {
		if (paramMap != null) {
			LinkedHashMap<String, String> map = req.getParamMap();
			if (map != null) {
				map.putAll(paramMap);
			} else {
				req.setParamMap(paramMap);
			}
		}
		return this;
	}

	public RequestBuilder header(String key, String value) {
		req.addHeader(key, value);
		return this;
	}

	public RequestBuilder header(LinkedHashMap<String, String> headers) {
		if (headers != null) {
			LinkedHashMap<String, String> map = req.getHeaders();
			if (map != null) {
				map.putAll(headers);
			} else {
				req.setHeaders(headers);
			}
		}
		return this;
	}

	public Response get() {
		req.setMethod(HttpMethod.Get);
		return send();
	}

	public Response put() {
		req.setMethod(HttpMethod.Put);
		return send();
	}

	public Response post() {
		req.setMethod(HttpMethod.Post);
		return send();
	}

	public Response delete() {
		req.setMethod(HttpMethod.Delete);
		return send();
	}

	public Request create() {
		return req;
	}

	public String getString() {
		return send().getString();
	}

	public byte[] getBytes() {
		return send().getBytes();
	}

	public <T> T getObject(Class<T> claxx) {
		return send().getObject(claxx);
	}

	public Response send() {
		return client.execute(req);
	}
}
