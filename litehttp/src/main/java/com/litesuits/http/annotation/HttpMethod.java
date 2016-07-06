package com.litesuits.http.annotation;

import com.litesuits.http.request.param.HttpMethods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MaTianyu
 * @date 2015-04-26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMethod {
    HttpMethods value();
}
