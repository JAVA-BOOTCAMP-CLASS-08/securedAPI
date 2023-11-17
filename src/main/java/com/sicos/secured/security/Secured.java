package com.sicos.secured.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {

	String[] validateMethod() default {Constants.TOKEN};
	
	String[] roles() default {};
	
	/**
	 * Si este valor es TRUE y estamos utilizando Token JWT como validateMethod el token sera utilizado una unica vez
	 * @return
	 */
	boolean unique() default false;
}
