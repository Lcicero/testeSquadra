package com.br.testesquadra.util.querybuilder;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Inserts the given expression in the where clause (or having clause, if {@literal @Having} is present) if the parameter value is {@link Condition#TRUE}.
 */
@Target({ METHOD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface IfTrue {
	/**
	 * Expression to use if this condition is met.
	 *
	 * @return Expression to use if this condition is met.
	 */
	public String value();
}
