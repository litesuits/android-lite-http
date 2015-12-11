package com.litesuits.http.request.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.litesuits.http.request.Request;

/**
 * help to build custom parameters for {@link Request}
 * 
 * @author MaTianyu
 * 2014-1-1上午2:45:11
 */
public interface CustomHttpParam {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CustomValueBuilder {}

	@CustomValueBuilder
	public CharSequence buildValue();

}
