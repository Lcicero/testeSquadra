package com.br.testesquadra.util.type;

import java.util.Collection;

/**
 * A collection type is a collection whose members can't be individually addressed. This includes straight java.util.Collection implementations and java.util.Set.
 */
@SuppressWarnings("rawtypes")
public class CollectionType implements TypeI {

	/** The raw collection type. */
	public Class<? extends Collection> containerType;

	/** The element type. */
	public TypeI elementType;

	/**
	 * {@inheritDoc}
	 */
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
		return this.containerType + "<" + this.elementType + ">";
	}
}
