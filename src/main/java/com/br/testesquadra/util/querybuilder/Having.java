package com.br.testesquadra.util.querybuilder;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Modifies the behaviour of expressions attached to the property: instead of being added to the where clause, they will be added to the having clause of the select expression.
 */
@Target({ METHOD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Having {
}
