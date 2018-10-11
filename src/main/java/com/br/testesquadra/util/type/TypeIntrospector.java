package com.br.testesquadra.util.type;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class introspects a type and provides a higher level views of that type.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TypeIntrospector {

	private static final Pattern TYPE_NAME = Pattern
			.compile("^\\s*\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*" + "(\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*\\s*");
	private static ConcurrentHashMap<ResolvedKey, TypeI> resolved = new ConcurrentHashMap<>();

	/**
	 * Private constructor to avoid instanciation of the class. This class should be used only in a static form.
	 */
	private TypeIntrospector() {

	}

	/**
	 * Resolves a generic type.
	 * 
	 * @param type
	 *            Generic type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If type is of an unknown subtype of Type.
	 */
	private static TypeI resolve(final Type type, final Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		if (type instanceof Class) {
			return resolve((Class) type, typeArguments, (Type[]) null);
		} else if (type instanceof ParameterizedType) {
			return resolve((ParameterizedType) type, typeArguments);
		} else if (type instanceof GenericArrayType) {
			return resolve((GenericArrayType) type, typeArguments);
		} else if (type instanceof TypeVariable) {
			return resolve((TypeVariable) type, typeArguments);
		} else if (type instanceof WildcardType) {
			return resolve((WildcardType) type, typeArguments);
		} else {
			throw new IllegalArgumentException("Don't know how to handle " + type.getClass());
		}
	}

	/**
	 * Resolves a wildcard generic type.
	 * 
	 * @param type
	 *            Generic type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If type is an unbound wildcard.
	 */
	private static TypeI resolve(final WildcardType type, final Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		if (type.getLowerBounds().length > 0) {
			return resolve(type.getLowerBounds()[0], typeArguments);
		} else if (type.getUpperBounds().length > 0) {
			return resolve(type.getUpperBounds()[0], typeArguments);
		} else {
			throw new IllegalArgumentException("Unbounded wildcard " + type);
		}
	}

	/**
	 * Resolves a type variable generic type.
	 * 
	 * @param type
	 *            Generic type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If there are no type arguments bound to this variable.
	 */
	private static TypeI resolve(final TypeVariable type, final Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		final Type instantiated = typeArguments.get(type);
		if (instantiated != null && !instantiated.equals(type)) {
			return resolve(instantiated, typeArguments);
		} else if (type.getBounds().length == 1) {
			return resolve(type.getBounds()[0], typeArguments);
		} else {
			throw new IllegalArgumentException("Uninstantiated type variable " + type.getName() + " at " + type.getGenericDeclaration());
		}
	}

	/**
	 * Resolves a generic array type.
	 * 
	 * @param type
	 *            Generic type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 */
	private static TypeI resolve(final GenericArrayType type, final Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		final IndexedType result = new IndexedType();
		result.elementType = resolve(type.getGenericComponentType(), typeArguments);
		final Object temp = Array.newInstance(result.elementType.getRawType(), 0);
		result.containerType = temp.getClass();
		return result;
	}

	/**
	 * Resolves a parameterized generic type's property type.
	 * 
	 * @param type
	 *            Generic type.
	 * @param property
	 *            Property name.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If type is not a bean type or doesn't expose a property by the given name.
	 */
	public static TypeI resolve(final ParameterizedType type, final String property) throws IntrospectionException {
		final TypeI resolved = resolve(type);
		if (!(resolved instanceof BeanType)) {
			throw new IllegalArgumentException("Type " + type.getRawType() + " is not a bean type");
		}
		final BeanType.Property prop = ((BeanType) resolved).properties.get(property);
		if (prop == null) {
			throw new IllegalArgumentException("Unknown property " + property + " for bean " + type.getRawType());
		}
		return prop.type;
	}

	/**
	 * Resolves a parameterized generic type.
	 * 
	 * @param type
	 *            Generic type.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 */
	public static TypeI resolve(final ParameterizedType type) throws IntrospectionException {
		final Map<TypeVariable, Type> typeArguments = new HashMap<>();
		return resolve(type, typeArguments);
	}

	/**
	 * Resolves a parameterized generic type.
	 * 
	 * @param type
	 *            Generic type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 */
	private static TypeI resolve(final ParameterizedType type, Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		typeArguments = merge(typeArguments, type);
		final ResolvedKey key = new ResolvedKey(type, typeArguments);

		if (resolved.containsKey(key)) {
			return resolved.get(key);
		}

		final Class<?> rawType = (Class) type.getRawType();
		if (rawType.isArray()) {
			final IndexedType result = new IndexedType();
			resolved.put(key, result);
			result.containerType = rawType;
			result.elementType = resolve(rawType.getComponentType(), typeArguments);
			return result;
		} else if (isSimple(rawType)) {
			final SimpleType result = new SimpleType();
			resolved.put(key, result);
			result.type = rawType;
			return result;
		} else if (List.class.isAssignableFrom(rawType)) {
			final IndexedType result = new IndexedType();
			resolved.put(key, result);
			result.containerType = rawType;
			result.elementType = getCollectionElementType(type, typeArguments);
			return result;
		} else if (Collection.class.isAssignableFrom(rawType)) {
			final CollectionType result = new CollectionType();
			resolved.put(key, result);
			result.containerType = (Class<? extends Collection>) rawType;
			result.elementType = getCollectionElementType(type, typeArguments);
			return result;
		} else if (Map.class.isAssignableFrom(rawType)) {
			final KeyedType result = new KeyedType();
			resolved.put(key, result);
			result.containerType = (Class<? extends Map>) rawType;
			final TypeI[] mapTypes = getMapElementTypes(type, typeArguments);
			result.keyType = mapTypes[0];
			result.valueType = mapTypes[1];
			return result;
		} else {
			final BeanType result = new BeanType();
			resolved.put(key, result);
			result.type = rawType;
			for (final PropertyDescriptor desc : Introspector.getBeanInfo(rawType).getPropertyDescriptors()) {
				if (desc.getName().equals("class")) {
					continue;
				}
				Type propType = null;
				final Method getter = desc.getReadMethod();
				final Method setter = desc.getWriteMethod();
				if (getter != null) {
					propType = getter.getGenericReturnType();
				}
				if (setter != null) {
					if (propType == null) {
						propType = setter.getGenericParameterTypes()[0];
					}
				}
				result.properties.put(desc.getName(), new BeanType.Property(getter, setter, resolve(propType, typeArguments)));
			}
			return result;
		}
	}

	/**
	 * Resolves the type of the given property of the given class with the given type arguments.
	 * 
	 * @param rawType
	 *            Raw type.
	 * @param property
	 *            Property name
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If type is not a bean type or doesn't expose a property by the given name.
	 */
	public static TypeI resolve(final Class rawType, final String property, final Type... adhocArguments) throws IntrospectionException {
		final TypeI resolved = resolve(rawType, adhocArguments);
		if (!(resolved instanceof BeanType)) {
			throw new IllegalArgumentException("Type " + rawType + " is not a bean type");
		}
		final BeanType.Property prop = ((BeanType) resolved).properties.get(property);
		if (prop == null) {
			throw new IllegalArgumentException("Unknown property " + property + " for bean " + rawType);
		}
		return prop.type;
	}

	public static TypeI resolve(final String typeName) throws IntrospectionException, ClassNotFoundException {
		final StringBuilder parse = new StringBuilder(typeName);
		final Class rawType = readType(parse);
		if (parse.length() == 0) {
			return resolve(rawType);
		} else if (parse.charAt(0) == '<') {
			final ArrayList<Class> args = new ArrayList<>();
			parse.deleteCharAt(0);
			args.add(readType(parse));
			while (parse.charAt(0) == ',') {
				parse.deleteCharAt(0);
				args.add(readType(parse));
			}
			if (parse.charAt(0) != '>') {
				throw new IllegalArgumentException("Expected \">\", got " + parse);
			}
			return resolve(rawType, args.toArray(new Class[args.size()]));
		} else {
			throw new IllegalArgumentException("Expected \"<\", got " + parse);
		}
	}

	private static Class readType(final StringBuilder input) throws ClassNotFoundException {
		final Matcher m = TYPE_NAME.matcher(input);
		if (m.find()) {
			final String result = m.group();
			input.delete(0, m.end());
			return loadClass(result.trim());
		} else {
			throw new IllegalArgumentException("Expected type name, got " + input);
		}
	}

	private static Class loadClass(String name) throws ClassNotFoundException {
		ClassNotFoundException exception = null;
		try {
			return Class.forName(name);
		} catch (final ClassNotFoundException e) {
			exception = e;
		}

		int lastDot = name.lastIndexOf('.');
		if (lastDot < 0) {
			try {
				return Class.forName("java.lang." + name);
			} catch (final ClassNotFoundException e) {
				throw exception;
			}
		} else {
			boolean done = false;
			while (!done) {
				name = name.substring(0, lastDot) + "$" + name.substring(lastDot + 1);
				try {
					return Class.forName(name);
				} catch (final ClassNotFoundException e) {
					lastDot = name.lastIndexOf('.');
					done = lastDot < 0;
				}
			}
			throw exception;
		}
	}

	/**
	 * Resolves a raw type with the given type arguments.
	 * 
	 * @param rawType
	 *            Raw type.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 */
	public static TypeI resolve(final Class rawType, final Type... adhocArguments) throws IntrospectionException {
		final Map<TypeVariable, Type> typeArguments = new HashMap<>();
		return resolve(rawType, typeArguments, adhocArguments);
	}

	/**
	 * Resolves a raw type with the given type arguments.
	 * 
	 * @param rawType
	 *            Raw type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Resolved TypeI instance.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 */
	private static TypeI resolve(final Class rawType, Map<TypeVariable, Type> typeArguments, final Type... adhocArguments) throws IntrospectionException {
		typeArguments = merge(typeArguments, rawType);
		if (adhocArguments != null) {
			final TypeVariable[] variables = rawType.getTypeParameters();
			for (int i = 0; i < variables.length; i++) {
				if (i < adhocArguments.length) {
					typeArguments.put(variables[i], adhocArguments[i]);
				}
			}
		}

		final ResolvedKey key = new ResolvedKey(rawType, typeArguments);
		if (resolved.containsKey(key)) {
			return resolved.get(key);
		}

		if (rawType.isArray()) {
			final IndexedType result = new IndexedType();
			resolved.put(key, result);
			result.containerType = rawType;
			result.elementType = resolve(rawType.getComponentType(), typeArguments);
			return result;
		} else if (isSimple(rawType)) {
			final SimpleType result = new SimpleType();
			resolved.put(key, result);
			result.type = rawType;
			return result;
		} else if (List.class.isAssignableFrom(rawType)) {
			final IndexedType result = new IndexedType();
			resolved.put(key, result);
			result.containerType = rawType;
			result.elementType = getCollectionElementType(rawType, typeArguments);
			return result;
		} else if (Collection.class.isAssignableFrom(rawType)) {
			final CollectionType result = new CollectionType();
			resolved.put(key, result);
			result.containerType = rawType;
			result.elementType = getCollectionElementType(rawType, typeArguments);
			return result;
		} else if (Map.class.isAssignableFrom(rawType)) {
			final KeyedType result = new KeyedType();
			resolved.put(key, result);
			result.containerType = rawType;
			final TypeI[] mapTypes = getMapElementTypes(rawType, typeArguments);
			result.keyType = mapTypes[0];
			result.valueType = mapTypes[1];
			return result;
		} else {
			final BeanType result = new BeanType();
			resolved.put(key, result);
			result.type = rawType;
			for (final PropertyDescriptor desc : Introspector.getBeanInfo(rawType).getPropertyDescriptors()) {
				if (desc.getName().equals("class")) {
					continue;
				}
				Type propType = null;
				final Method getter = getGetterMethod(desc, rawType);
				final Method setter = desc.getWriteMethod();
				if (getter != null) {
					propType = getter.getGenericReturnType();
				}
				if (setter != null) {
					if (propType == null) {
						propType = setter.getGenericParameterTypes()[0];
					}
				}
				if (propType != null) {
					result.properties.put(desc.getName(), new BeanType.Property(getter, setter, resolve(propType, typeArguments)));
				}
			}
			return result;
		}
	}

	/**
	 * Figures out the element type of an Iterator type.
	 * 
	 * @param type
	 *            Iterator type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Element type of an Iterator type.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If it's impossible to determine the element type.
	 * @throws RuntimeException
	 *             Wraps any thrown exceptions.
	 */
	private static TypeI getIteratorElementType(final Type type, Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		Class rawType;
		if (type instanceof ParameterizedType) {
			rawType = (Class) ((ParameterizedType) type).getRawType();
			typeArguments = merge(typeArguments, type);
		} else if (type instanceof Class) {
			rawType = (Class) type;
		} else {
			throw new IllegalArgumentException("Can't get iterator element type for " + type);
		}

		Method m;
		try {
			m = rawType.getMethod("next");
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		return resolve(m.getGenericReturnType(), typeArguments);
	}

	/**
	 * Figures out the element type of a Collection type.
	 * 
	 * @param type
	 *            Collection type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Element type of a Collection type.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If it's impossible to determine the element type.
	 * @throws RuntimeException
	 *             Wraps any thrown exceptions.
	 */
	private static TypeI getCollectionElementType(final Type type, Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		Class rawType;
		if (type instanceof ParameterizedType) {
			rawType = (Class) ((ParameterizedType) type).getRawType();
			typeArguments = merge(typeArguments, type);
		} else if (type instanceof Class) {
			rawType = (Class) type;
		} else {
			throw new IllegalArgumentException("Can't get collection element type for " + type);
		}

		Method m;
		try {
			m = rawType.getMethod("iterator");
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return getIteratorElementType(m.getGenericReturnType(), typeArguments);
	}

	/**
	 * Figures out the key and value types of a Map type.
	 * 
	 * @param type
	 *            Map type.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 * 
	 * @return Array containing key and element type in this order.
	 * 
	 * @throws IntrospectionException
	 *             If bean introspection fails.
	 * @throws IllegalArgumentException
	 *             If it's impossible to determine the element type.
	 * @throws RuntimeException
	 *             Wraps any thrown exceptions.
	 */
	private static TypeI[] getMapElementTypes(final Type type, Map<TypeVariable, Type> typeArguments) throws IntrospectionException {
		Class rawType;
		if (type instanceof ParameterizedType) {
			rawType = (Class) ((ParameterizedType) type).getRawType();
			typeArguments = merge(typeArguments, type);
		} else if (type instanceof Class) {
			rawType = (Class) type;
		} else {
			throw new IllegalArgumentException("Can't get map element types for " + type);
		}

		Type keySet;
		Type values;
		try {
			keySet = rawType.getMethod("keySet").getGenericReturnType();
			values = rawType.getMethod("values").getGenericReturnType();
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return new TypeI[] { getCollectionElementType(keySet, typeArguments), getCollectionElementType(values, typeArguments) };
	}

	/**
	 * Adds a parameterized type's type arguments to the given map.
	 * 
	 * @param p
	 *            Type to extract type arguments from.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 */
	private static void addTypeVariables(final ParameterizedType p, final Map<TypeVariable, Type> typeArguments) {
		addTypeVariables((Class) p.getRawType(), typeArguments);
		final TypeVariable[] variables = ((Class) p.getRawType()).getTypeParameters();
		final Type[] actual = p.getActualTypeArguments();
		for (int i = 0; i < actual.length; i++) {
			typeArguments.put(variables[i], actual[i]);
		}
		if (p.getOwnerType() != null) {
			addTypeVariables(p.getOwnerType(), typeArguments);
		}
	}

	/**
	 * Adds a type's type arguments to the given map.
	 * 
	 * @param c
	 *            Type to extract type arguments from.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 */
	private static void addTypeVariables(final Class c, final Map<TypeVariable, Type> typeArguments) {
		for (final TypeVariable v : c.getTypeParameters()) {
			typeArguments.put(v, v);
		}
		for (final Type intf : c.getGenericInterfaces()) {
			addTypeVariables(intf, typeArguments);
		}
		if (c.getGenericSuperclass() != null) {
			addTypeVariables(c.getGenericSuperclass(), typeArguments);
		}
	}

	/**
	 * Returns a new map containing all of original's entries plus any new type arguments introspected from type.
	 * 
	 * @param original
	 *            Generic type arguments in scope.
	 * @param type
	 *            Type to extract type arguments from.
	 * 
	 * @return New set of generic type arguments in scope.
	 */
	private static Map<TypeVariable, Type> merge(final Map<TypeVariable, Type> original, final Type type) {
		final Map<TypeVariable, Type> result = new HashMap<>();
		result.putAll(original);
		addTypeVariables(type, result);
		return result;
	}

	/**
	 * Adds a type's type arguments to the given map.
	 * 
	 * @param t
	 *            Type to extract type arguments from.
	 * @param typeArguments
	 *            Generic type arguments in scope.
	 */
	private static void addTypeVariables(final Type t, final Map<TypeVariable, Type> typeArguments) {
		if (t instanceof Class) {
			addTypeVariables((Class) t, typeArguments);
		} else if (t instanceof ParameterizedType) {
			addTypeVariables((ParameterizedType) t, typeArguments);
		}
	}

	/**
	 * Determines if the given raw type is a simple type (primitives, strings, dates, etc.).
	 * 
	 * @param type
	 *            Type to evaluate.
	 * 
	 * @return True if the given type is a simple type.
	 */
	private static boolean isSimple(final Class<?> type) {
		return type.isPrimitive() || CharSequence.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type)
				|| Calendar.class.isAssignableFrom(type) || Boolean.class == type || Character.class == type || Class.class == type || type.isEnum();
	}

	/**
	 * Gets the getter method for the property given by the specified PropertyDescriptor. First will try desc.getReadMethod(). If it returns null and the property type is
	 * java.langBoolean then tries to get an "is" method.
	 * 
	 * @param desc
	 *            The property descriptor.
	 * @param rawType
	 *            The property type Class
	 * @return The getter method.
	 */
	private static Method getGetterMethod(final PropertyDescriptor desc, final Class rawType) {
		Method result = desc.getReadMethod();
		if (result == null) {
			try {
				final Class propType = desc.getPropertyType();
				if (propType.equals(Boolean.class)) {
					String propName = desc.getName();
					propName = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
					final Method isMethod = rawType.getDeclaredMethod("is" + propName, (Class[]) null);
					if (isMethod != null) {
						final Class returnType = isMethod.getReturnType();
						if (returnType != null && returnType.equals(Boolean.class)) {
							result = isMethod;
						}
					}
				}
			} catch (final Exception exc) {
				// Coudn't find the method isPropName so will return null
			}
		}
		return result;
	}

}
