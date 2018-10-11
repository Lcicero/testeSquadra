package com.br.testesquadra.util.type;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A bean type is an structured type that exposes properties.
 */
public class BeanType implements TypeI {
	/** Bean raw type. */
	public Class<?> type;

	/** Map of property descriptors indexed by property names. */
	public Map<String, Property> properties = new HashMap<>();

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

	@Override
	public String toString() {
		return this.type.getName();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof BeanType)) {
			return false;
		}
		final BeanType other = (BeanType) obj;
		return this.type == other.type && this.properties.equals(other.properties);
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	/**
	 * A bean property descriptor.
	 */
	public static class Property {
		/** Property getter method. */
		public Method getter;

		/** Property setter method. */
		public Method setter;

		/** Property type. */
		public TypeI type;

		/**
		 * Creates a new Property object.
		 *
		 * @param getter
		 *            Property getter method.
		 * @param setter
		 *            Property setter method.
		 * @param type
		 *            Property type.
		 */
		Property(final Method getter, final Method setter, final TypeI type) {
			this.getter = getter;
			this.setter = setter;
			this.type = type;
		}

		/**
		 * Compares two objects for equality.
		 *
		 * @param obj
		 *            Target object.
		 *
		 * @return True if this object and the target object are equal.
		 */
		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			} else if (!(obj instanceof Property)) {
				return false;
			}
			final Property other = (Property) obj;
			return this.getter == other.getter && this.setter == other.setter && this.type.equals(other.type);
		}

		/**
		 * This object's hashcode.
		 *
		 * @return This object's hashcode.
		 */
		@Override
		public int hashCode() {
			int hash = this.type.hashCode();
			hash = hash * 37 + (this.getter == null ? 0 : this.getter.hashCode());
			hash = hash * 37 + (this.setter == null ? 0 : this.setter.hashCode());
			return hash;
		}
	}
}
