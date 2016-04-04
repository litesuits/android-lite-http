package com.litesuits.http.request.param;

/**
 * support method
 *
 * @author MaTianyu
 *         2014-1-1下午9:51:59
 */
public enum HttpMethods {
	/* ******************* Http Get(Query) Request *************/
    /**
     * get
     */
    Get("GET"),
    /**
     * get http header only
     */
    Head("HEAD"),
    /**
     * debug
     */
    Trace("TRACE"),
    /**
     * query
     */
    Options("OPTIONS"),
    /**
     * delete
     */
    Delete("DELETE"),
	/* ******************* Http Upate(Entity Enclosing) Request *************/
    /**
     * update
     */
    Put("PUT"),
    /**
     * add
     */
    Post("POST"),
    /**
     * incremental update
     */
    Patch("PATCH");

    private String methodName;

    HttpMethods(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
