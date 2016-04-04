package com.litesuits.http.request.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * marked for java field that exclude from http parameter.
 * 
 * @author MaTianyu
 * 2014-1-19下午11:57:47
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NonHttpParam {}
