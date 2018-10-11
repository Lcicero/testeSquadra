package com.br.testesquadra.util.querybuilder;

/**
 * Classes that want to provide a non-static query template to the {@link QueryBuilder} should implement this interface <em>instead of</em> being annotated with {@link StaticQuery}
 * .
 */
public interface DynamicQuery {
	/**
	 * The query template.
	 *
	 * @return The query template.
	 */
	public String query();
}
