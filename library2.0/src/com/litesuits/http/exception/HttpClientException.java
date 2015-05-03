package com.litesuits.http.exception;

/**
 * exception with bad request.
 * <p/>
 * Start--1(build request)-->Reqeust--2(connect network)-->Response--3(handle response)-->End
 *
 * @author MaTianyu
 *         2014-1-2上午12:53:13
 */
public class HttpClientException extends HttpException {
    private static final long serialVersionUID = -1710690396078830713L;
    private ClientException exceptionType;

    public HttpClientException(ClientException clientExp) {
        super(clientExp.toString());
        exceptionType = clientExp;
    }

    public HttpClientException(Throwable cause) {
        super(cause.toString(), cause);
        exceptionType = ClientException.SomeOtherException;
    }

    public HttpClientException(Throwable cause, ClientException type) {
        super(cause.toString(), cause);
        exceptionType = type;
    }

    public ClientException getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ClientException exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public String toString() {
        return exceptionType.toString();
    }


}
