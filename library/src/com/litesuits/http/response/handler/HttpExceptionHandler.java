package com.litesuits.http.response.handler;

import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpClientException.ClientException;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpNetException.NetException;
import com.litesuits.http.exception.HttpServerException;
import com.litesuits.http.exception.HttpServerException.ServerException;

/**
 * Handle Response on UI Thread
 * 
 * @author MaTianyu
 * 2014-2-26下午9:02:09
 */
public abstract class HttpExceptionHandler {

	public HttpExceptionHandler handleException(HttpException e) {
		if (e != null) {
			if (e instanceof HttpClientException) {
				HttpClientException ce = ((HttpClientException) e);
				onClientException(ce, ce.getExceptionType());
			} else if (e instanceof HttpNetException) {
				HttpNetException ne = ((HttpNetException) e);
				onNetException(ne, ne.getExceptionType());
			} else if (e instanceof HttpServerException) {
				HttpServerException se = ((HttpServerException) e);
				onServerException(se, se.getExceptionType(), se.getHttpStatus());
			} else {
				throw new RuntimeException("Unkonwn HttpException");
			}
		}
		return this;
	}

	/**
	 * 比如 URL为空，构建参数异常以及请求过程中遇到的其他客户端异常。
	 * @param e
	 */
	protected abstract void onClientException(HttpClientException e, ClientException type);

	/**
	 * 比如 无网络，网络不稳定，该网络类型已被禁用等。
	 * @param e
	 */
	protected abstract void onNetException(HttpNetException e, NetException type);

	/**
	 * 这个时候，连接是成功的。http header已经返回，但是status code是400~599之间。
	 * [400-499]：因为客户端原因，服务器拒绝服务
	 * [500~599]：基本是服务器内部错误或者网关异常造成的
	 * @param e
	 */
	protected abstract void onServerException(HttpServerException e, ServerException type, HttpStatus status);
}
