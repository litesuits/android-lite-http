package com.litesuits.http.exception;

/**
 * exception with bad request.
 * 
 * Start--1(build request)-->Reqeust--2(connect network)-->Response--3(handle
 * response)-->End
 * 
 * @author MaTianyu
 * 2014-1-2上午12:53:13
 */
public class HttpClientException extends HttpException {
	private static final long serialVersionUID = -1710690396078830713L;
	private ClientException exceptionType;

	public enum ClientException {
		UrlIsNull("Url Is Null ", "Url 为空 "),
		OtherException("Unknown Exception", "不可预知错误 ");

		public String reason;
		public String chiReason;

		ClientException(String name, String chiName) {
			this.reason = name;
			this.chiReason = chiName;
		}
	}

	public HttpClientException(ClientException clientExp) {
		super(useChinese ? clientExp.chiReason : clientExp.reason);
		exceptionType = clientExp;
	}

	public HttpClientException(Throwable cause) {
		super(cause.toString(), cause);
		exceptionType = ClientException.OtherException;
	}

	public ClientException getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(ClientException exceptionType) {
		this.exceptionType = exceptionType;
	}
}
