package com.litesuits.http.exception;

/**
 * Base HttpException
 * Start--1(build request)-->Reqeust--2(connect network)-->Response--3(handle response)-->End
 *
 * @author MaTianyu
 *         2014-1-2上午12:51:38
 */
public abstract class HttpException extends Exception {
    private static final long serialVersionUID = -8585446012573642784L;
    public static boolean printStackTrace = true;
    protected boolean handled = true;

    public boolean isHandled() {
        return handled;
    }

    public <T extends HttpException> T setHandled(boolean handled) {
        this.handled = handled;
        return (T) this;
    }

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

    @Override
    public String toString() {
        return "HttpException{" +
               "handled=" + handled +
               "} " + super.toString();
    }
}
