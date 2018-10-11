package com.br.testesquadra.util.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * A key to the resolved types cache.
 * 
 * @author Fabiane Bizinella Nardon
 * @author Rogério Gatto
 * @version $Revision: 37602 $
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class ResolvedKey {

	private final Type type;
	private final Map<TypeVariable, Type> typeArguments = new HashMap<>();

	/**
	 * Creates a new ResolvedKey object.
	 * 
	 * @param type
	 *            Type this key referes to.
	 * @param typeArguments
	 *            Type arguments in scope.
	 */
	public ResolvedKey(final Type type, final Map<TypeVariable, Type> typeArguments) {
		this.type = type;
		this.typeArguments.putAll(typeArguments);
		this.typeArguments.keySet().retainAll(this.getArguments(type));
		this.addNeededTypeArguments(typeArguments);
	}

	/**
	 * Adds type arguments that are needed to solve the type.
	 * 
	 * @param typeArguments
	 *            the list of all solved typeArguments.
	 */
	private void addNeededTypeArguments(final Map<TypeVariable, Type> typeArguments) {
		if (this.typeArguments == null) {
			return;
		}

		boolean added = true;
		while (added) {
			added = false;
			// Creates a copy of the array so we can iterate of it and modify
			// the original this.typeArguments
			final HashSet copyOfTypeArguments = new HashSet();
			copyOfTypeArguments.addAll(this.typeArguments.keySet());
			final Iterator it = copyOfTypeArguments.iterator();
			while (it.hasNext()) {
				final Type value = this.typeArguments.get(it.next());
				added = this.addNeededTypeArgument(value, typeArguments);
			}
		}
	}

	private boolean addNeededTypeArgument(final Type type, final Map<TypeVariable, Type> typeArguments) {
		if (type instanceof TypeVariable && this.typeArguments.get(type) == null) {
			this.typeArguments.put((TypeVariable) type, typeArguments.get(type));
			return true;
		} else if (type instanceof ParameterizedType && this.typeArguments.get(type) == null) {
			final Type[] types = ((ParameterizedType) type).getActualTypeArguments();
			for (final Type t : types) {
				return this.addNeededTypeArgument(t, typeArguments);
			}
		} else if (type instanceof GenericArrayType && this.typeArguments.get(type) == null) {
			final Type t = ((GenericArrayType) type).getGenericComponentType();
			return this.addNeededTypeArgument(t, typeArguments);
		} else if (type instanceof WildcardType && this.typeArguments.get(type) == null) {
			final Type[] types = ((WildcardType) type).getUpperBounds();
			if (types != null && types.length > 0) {
				return this.addNeededTypeArgument(types[0], typeArguments);
			}
		}
		return false;
	}

	private Class<?> findRawType(final Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) type).getRawType();
		} else if (type instanceof TypeVariable) {
			return this.findRawType(((TypeVariable) type).getBounds()[0]);
		} else if (type instanceof WildcardType) {
			return this.findRawType(((WildcardType) type).getUpperBounds()[0]);
		} else if (type instanceof GenericArrayType) {
			return this.findRawType(((GenericArrayType) type).getGenericComponentType());
		} else {
			return null;
		}
	}

	private HashSet<TypeVariable> getArguments(final Type type) {
		final HashSet<TypeVariable> vars = new HashSet<>();
		this.getArguments(this.findRawType(type), vars);
		return vars;
	}

	private void getArguments(final Class<?> type, final HashSet<TypeVariable> vars) {
		vars.addAll(Arrays.asList(type.getTypeParameters()));
		if (!Modifier.isStatic(type.getModifiers()) && type.getEnclosingClass() != null) {
			this.getArguments(type.getEnclosingClass(), vars);
		}
	}

	/**
	 * This object's hashcode.
	 * 
	 * @return This object's hashcode.
	 */
	@Override
	public int hashCode() {
		return this.type.hashCode() * 31 + this.typeArguments.hashCode();
	}

	/**
	 * Compares two objects for equality.
	 * 
	 * @param other
	 *            Target object.
	 * 
	 * @return True if this object and the target object are equal.
	 */
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ResolvedKey)) {
			return false;
		}

		final ResolvedKey key = (ResolvedKey) other;
		return this.type.equals(key.type) && this.typeArguments.equals(key.typeArguments);
	}

}
