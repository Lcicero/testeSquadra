package com.br.testesquadra.util.type;

import java.util.Map;

/**
 * A keyed type is a collection whose elements can be individually addressed by an object key, such as java.util.Map instances.
 */
@SuppressWarnings("rawtypes")
public class KeyedType implements TypeI {

	/** The raw collection type. */
	public Class<? extends Map> containerType;

	/** The key type. */
	public TypeI keyType;

	/** The mapped value type. */
	public TypeI valueType;

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
		return this.valueType.getContentType();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof KeyedType)) {
			return false;
		}
		final KeyedType other = (KeyedType) obj;
		return this.containerType == other.containerType && this.keyType == other.keyType && this.valueType == other.valueType;
	}

	@Override
	public int hashCode() {
		int hash = this.containerType.hashCode();
		hash = hash * 37 + this.keyType.hashCode();
		hash = hash * 37 + this.valueType.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return this.containerType + "<" + this.keyType + "," + this.valueType + ">";
	}
}
