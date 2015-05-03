package com.litesuits.http.exception;

/**
 * exception when network error happened.
 *
 * @author MaTianyu
 *         2014-1-2上午12:53:13
 */
public class HttpNetException extends HttpException {
    private static final long serialVersionUID = 4961807092977094093L;
    private NetException exceptionType;

    public HttpNetException(NetException netExp) {
        super(netExp.toString());
        exceptionType = netExp;
    }

    /**
     * 包裹其他异常，网络不稳定因素或者防火墙限制
     *
     * @param cause
     */
    public HttpNetException(Throwable cause) {
        super(cause.toString(), cause);
        exceptionType = NetException.NetworkUnstable;
    }

    public NetException getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(NetException exceptionType) {
        this.exceptionType = exceptionType;
    }



    @Override
    public String toString() {
        return exceptionType.toString();
    }
}
