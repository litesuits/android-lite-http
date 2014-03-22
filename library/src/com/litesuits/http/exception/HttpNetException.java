package com.litesuits.http.exception;

/**
 * exception when network error happened.
 * 
 * @author MaTianyu
 * 2014-1-2上午12:53:13
 */
public class HttpNetException extends HttpException {
	private static final long serialVersionUID = 4961807092977094093L;
	private NetException exceptionType;

	public enum NetException {
		NetworkError("Network Is Not Avilable", "暂无网络"),
		NetworkDisabled("Current Network Is Disabled By Your Setting", "你已设置禁用该网络类型"),
		UnReachable("Service UnreachNetwork Is Unstable", "无法访问或网络不稳定");
		public String reason;
		public String chiReason;

		NetException(String name, String chiName) {
			this.reason = name;
			this.chiReason = chiName;
		}

		public String getReason() {
			return useChinese ? chiReason : reason;
		}
	}

	public HttpNetException(NetException netExp) {
		super(useChinese ? netExp.chiReason : netExp.reason);
		exceptionType = netExp;
	}

	/**
	 * 包裹其他异常，网络不稳定因素或者防火墙限制
	 * @param cause
	 */
	public HttpNetException(Throwable cause) {
		super(cause.toString(), cause);
		exceptionType = NetException.UnReachable;
	}

	public NetException getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(NetException exceptionType) {
		this.exceptionType = exceptionType;
	}

}
