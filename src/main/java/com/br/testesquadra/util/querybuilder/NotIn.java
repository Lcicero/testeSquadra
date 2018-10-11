package com.br.testesquadra.util.querybuilder;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Creates an expression of the form <em>prop not in :param</em>.
 */
@Target({ METHOD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface NotIn {
	/**
	 * The persistent property to compare to. Defaults to the parameter name.
	 *
	 * @return The persistent property to compare to. Defaults to the parameter name.
	 */
	public String value() default "";
}
