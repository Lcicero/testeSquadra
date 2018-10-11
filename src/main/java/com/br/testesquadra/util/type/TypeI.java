package com.br.testesquadra.util.type;

/**
 * Represents java types, including generics supports, in a convenient way.
 */
public interface TypeI {

	/**
	 * The corresponding raw type as a java class.
	 *
	 * @return The corresponding raw type as a java class.
	 */
	public Class<?> getRawType();

	/**
	 * The content type as java class. If it is Collection, Map, Array, etc, returns the type of its elements. Otherwise, returns the raw type.
	 *
	 * @return The corresponding content type as a java class.
	 */
	public Class<?> getContentType();

	/**
	 * Implementations of this interface must override equals.
	 *
	 * @param obj
	 *            Object to compare to.
	 *
	 * @return True if this instance and the given object are equal.
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Implementations of this interface must override hashCode.
	 *
	 * @return Hashcode for this instance.
	 */
	@Override
	public int hashCode();
}
