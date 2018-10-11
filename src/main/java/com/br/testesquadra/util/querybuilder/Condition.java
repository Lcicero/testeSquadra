package com.br.testesquadra.util.querybuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Conditions that may trigger expression inclusion.
 */
public enum Condition {
	/**
	 * Triggers expression inclusion if the property value is a <code>boolean</code> or <code>Boolean</code> <code>true</code> value.
	 */
	TRUE {
		@Override
		public boolean eval(final Object value) {
			return isTrue(value);
		}
	},

	/**
	 * Triggers expression inclusion if the property value is a <code>boolean</code> or <code>Boolean</code> <code>false</code> value.
	 */
	FALSE {
		@Override
		public boolean eval(final Object value) {
			return isFalse(value);
		}
	},

	/**
	 * Triggers expression inclusion if the property value is either a <code>null</code> reference, a zero-length <code>String</code> or array, or an empty <code>Collection</code>
	 * or <code>Map</code>.
	 */
	EMPTY {
		@Override
		public boolean eval(final Object value) {
			return isEmpty(value);
		}
	},

	/**
	 * Triggers expression inclusion if the property value is NOT {@link #EMPTY} .
	 */
	NOT_EMPTY {
		@Override
		public boolean eval(final Object value) {
			return !isEmpty(value);
		}
	},

	/**
	 * Triggers expression inclusion if the property value is either {@link #TRUE} or {@link #EMPTY}.
	 */
	TRUE_OR_EMPTY {
		@Override
		public boolean eval(final Object value) {
			return isTrue(value) || isEmpty(value);
		}
	},

	/**
	 * Triggers expression inclusion if the property value is either {@link #FALSE} or {@link #EMPTY}.
	 */
	FALSE_OR_EMPTY {
		@Override
		public boolean eval(final Object value) {
			return isFalse(value) || isEmpty(value);
		}
	};

	/**
	 * Determines if the condition represented by this constant is met by the given value.
	 *
	 * @param value
	 *            Value to test.
	 * @return True if the condition was met.
	 */
	public abstract boolean eval(Object value);

	/**
	 * Returns the Condition corresponding to the given annotation class.
	 *
	 * @param ann
	 *            Annotation to test.
	 * @return Condition that should be met to trigger the inclusion of this annotation's expression in the generated query.
	 */
	public static final Condition forAnnotation(final Class<? extends Annotation> ann) {
		if (ann == IfEmpty.class) {
			return EMPTY;
		} else if (ann == IfNotEmpty.class) {
			return NOT_EMPTY;
		} else if (ann == IfTrue.class) {
			return TRUE;
		} else if (ann == IfFalse.class) {
			return FALSE;
		} else if (ann == IfTrueOrEmpty.class) {
			return TRUE_OR_EMPTY;
		} else if (ann == IfFalseOrEmpty.class) {
			return FALSE_OR_EMPTY;
		} else {
			return NOT_EMPTY;
		}
	}

	/**
	 * Tests if the given value is true.
	 *
	 * @param value
	 *            Value to test.
	 * @return True if the value is a Boolean with true value.
	 */
	private static final boolean isTrue(final Object value) {
		return value instanceof Boolean && ((Boolean) value).booleanValue();
	}

	/**
	 * Tests if the given value is false.
	 *
	 * @param value
	 *            Value to test.
	 * @return True if the value is a Boolean with false value.
	 */
	private static final boolean isFalse(final Object value) {
		return value instanceof Boolean && !((Boolean) value).booleanValue();
	}

	/**
	 * Tests if the given value is empty.
	 *
	 * @param value
	 *            Value to test.
	 * @return True if the value is empty.
	 */
	@SuppressWarnings("rawtypes")
	private static final boolean isEmpty(final Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof String) {
			return ((String) value).length() == 0;
		} else if (value instanceof Collection) {
			return ((Collection) value).isEmpty();
		} else if (value instanceof Map) {
			return ((Map) value).isEmpty();
		} else if (value.getClass().isArray()) {
			return Array.getLength(value) == 0;
		} else {
			return false;
		}
	}
}