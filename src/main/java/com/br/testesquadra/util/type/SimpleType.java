package com.br.testesquadra.util.type;

/**
 * A "simple" type, such as a String, Date, primitive, etc.
 */
public class SimpleType implements TypeI {
	/** This type's raw type. */
	public Class<?> type;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getRawType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getContentType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof SimpleType)) {
			return false;
		}
		return this.type == ((SimpleType) obj).type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	/**
	 * Returns a String representation of this object.
	 *
	 * @return A String representation of this object.
	 */
	@Override
	public String toString() {
		return this.type.getName();
	}
}
