package com.br.testesquadra.util.querybuilder;

import java.util.Map;

/**
 * Classes that want to provide an arbitrary number of query parameters, not only limited to the bean properties.
 *
 * @author Humberto
 * @version $Id$
 */
public interface CustomQueryParameters {
	/**
	 * Emit custom parameters.
	 * 
	 * @return Map where parameter values will be put, indexed by their names.
	 */
	public Map<String, Object> emitCustomParameters();
}
