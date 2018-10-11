package com.br.testesquadra.util.querybuilder;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Insert the value of this property as a where clause expression (or having clause, if @Having is present) if this property's value is a String with length greater than zero.
 */
@Target({ METHOD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Literal {
}
