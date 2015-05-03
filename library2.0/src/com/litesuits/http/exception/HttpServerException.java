package com.litesuits.http.exception;

import com.litesuits.http.data.HttpStatus;

/**
 * exception that happen in server.
 *
 * @author MaTianyu
 *         2014-1-2上午12:53:13
 */
public class HttpServerException extends HttpException {
    private static final long serialVersionUID = 3695887939006497385L;
    private ServerException exceptionType;
    private HttpStatus      status;

    public HttpServerException(ServerException e) {
        super(e.toString());
        exceptionType = e;
    }

    public HttpServerException(HttpStatus status) {
        super(status.toString());
        this.status = status;
        if (status.getCode() >= 500) {
            exceptionType = ServerException.ServerInnerError;
        } else {
            exceptionType = ServerException.ServerRejectClient;
        }
    }

    public ServerException getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ServerException exceptionType) {
        this.exceptionType = exceptionType;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public String toString() {
        return exceptionType + ", " + status;
    }


}
