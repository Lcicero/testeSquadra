package com.br.testesquadra.util.querybuilder;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Classes that want to provide a immutable query template to the {@link QueryBuilder} should be annotated with this annotation.
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface StaticQuery {
	/**
	 * The query template.
	 *
	 * @return The query template.
	 */
	public String value();
}
