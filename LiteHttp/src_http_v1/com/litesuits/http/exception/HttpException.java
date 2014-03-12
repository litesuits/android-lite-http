package com.litesuits.http.exception;

import com.litesuits.http.LiteHttpClient;

/**
 * Base HttpException
 * 
 * @author MaTianyu
 *         2014-1-2上午12:51:38
 */
public abstract class HttpException extends Exception {
	private static final long serialVersionUID = -8585446012573642784L;
	public static boolean useChinese = LiteHttpClient.errorInChinese;
	public static boolean printStackTrace = true;

	public HttpException() {}

	public HttpException(String name) {
		super(name);
	}

	public HttpException(String name, Throwable cause) {
		super(name, cause);
	}

	public HttpException(Throwable cause) {
		super(cause);
	}
}
