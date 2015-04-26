package com.litesuits.http.request.param;

/**
 * support method 
 * 
 * @author MaTianyu
 * 2014-1-1下午9:51:59
 */
public enum HttpMethods {
	/* ******************* Http Get(Query) Request *************/
	/**
	 * get
	 */
	Get,
	/**
	 * get http header only
	 */
	Head,
	/**
	 * debug
	 */
	Trace,
	/**
	 * query 
	 */
	Options,
	/**
	 * delete
	 */
	Delete,
	/* ******************* Http Upate(Entity Enclosing) Request *************/
	/**
	 * update 
	 */
	Put,
	/**
	 * add 
	 */
	Post,
	/**
	 * incremental update
	 */
	Patch
}
