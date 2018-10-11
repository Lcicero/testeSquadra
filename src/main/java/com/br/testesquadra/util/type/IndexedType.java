package com.br.testesquadra.util.type;

/**
 * An indexed type is a collection whose elements can be individually addressed by an integer index, such as arrays and java.util.List instances.
 */
public class IndexedType implements TypeI {
	/** The raw collection or array type. */
	public Class<?> containerType;

	/** The element type. */
	public TypeI elementType;

	@Override
	public Class<?> getRawType() {
		return this.containerType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getContentType() {
		return this.elementType.getContentType();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof IndexedType)) {
			return false;
		}
		final IndexedType other = (IndexedType) obj;
		return this.containerType == other.containerType && this.elementType == other.elementType;
	}

	@Override
	public int hashCode() {
		int hash = this.containerType.hashCode();
		hash = hash * 37 + this.elementType.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return this.containerType.isArray() ? this.elementType + "[]" : this.containerType + "<" + this.elementType + ">";
	}
}
