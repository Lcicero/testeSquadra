package com.br.testesquadra.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.br.testesquadra.util.exception.SystemException;


public class UncheckedPropertyUtil {

	private static final String ILLEGAL_ACCESS_ERROR_MESSAGE = " method/attribute is not accessible on ";

	private static final String INVOCATION_TARGET_ERROR_MESSAGE = "Exception executing method/attribute on ";

	private static final String METHOD_NOT_FOUND_ERROR_MESSAGE = " method/attribute not found on ";

	private UncheckedPropertyUtil() {

	}

	public static Object getProperty(final Object source, final String property) {
		try {
			return PropertyUtils.getProperty(source, property);
		} catch (final NoSuchMethodException e) {
			final String message = property + METHOD_NOT_FOUND_ERROR_MESSAGE + source.getClass().getName();
			throw new SystemException(message, e);
		} catch (final IllegalAccessException e) {
			final String message = property + ILLEGAL_ACCESS_ERROR_MESSAGE + source.getClass().getName();
			throw new SystemException(message, e);
		} catch (final InvocationTargetException e) {
			throw new SystemException(INVOCATION_TARGET_ERROR_MESSAGE + source.getClass().getName(), e);
		}
	}

	public static void setProperty(final Object target, final String property, final Object value) {
		try {
			PropertyUtils.setProperty(target, property, value);
		} catch (final NoSuchMethodException e) {
			final String message = property + METHOD_NOT_FOUND_ERROR_MESSAGE + target.getClass().getName();
			throw new SystemException(message, e);
		} catch (final IllegalAccessException e) {
			final String message = property + ILLEGAL_ACCESS_ERROR_MESSAGE + target.getClass().getName();
			throw new SystemException(message, e);
		} catch (final InvocationTargetException e) {
			throw new SystemException(INVOCATION_TARGET_ERROR_MESSAGE + target.getClass().getName(), e);
		}
	}

	public static void copyProperties(final Object target, final Object source) {
		try {
			PropertyUtils.copyProperties(target, source);
		} catch (final NoSuchMethodException e) {
			final String message = METHOD_NOT_FOUND_ERROR_MESSAGE + target.getClass().getName();
			throw new SystemException(message, e);
		} catch (final IllegalAccessException e) {
			final String message = ILLEGAL_ACCESS_ERROR_MESSAGE + target.getClass().getName();
			throw new SystemException(message, e);
		} catch (final InvocationTargetException e) {
			throw new SystemException(INVOCATION_TARGET_ERROR_MESSAGE + target.getClass().getName(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Class getPropertyType(final Object source, final String property) {
		try {
			return PropertyUtils.getPropertyType(source, property);
		} catch (final NoSuchMethodException e) {
			final String message = property + METHOD_NOT_FOUND_ERROR_MESSAGE + source.getClass().getName();
			throw new SystemException(message, e);
		} catch (final IllegalAccessException e) {
			final String message = property + ILLEGAL_ACCESS_ERROR_MESSAGE + source.getClass().getName();
			throw new SystemException(message, e);
		} catch (final InvocationTargetException e) {
			throw new SystemException(INVOCATION_TARGET_ERROR_MESSAGE + source.getClass().getName(), e);
		}
	}

	public static void setHierarchicalProperty(final Object target, final String property, final Object value, final PropertyAction action) {

		if (target == null) {
			return;
		}

		final String[] props = StringUtils.split(property, '.');
		final LinkedList<String> hierarchicalProperties = new LinkedList<>();
		hierarchicalProperties.addAll(Arrays.asList(props));

		setHierarchicalProperty(target, hierarchicalProperties, value, action);

	}

	@SuppressWarnings("unchecked")
	private static void setHierarchicalProperty(final Object target, final LinkedList<String> properties, final Object value, final PropertyAction action) {

		final LinkedList<String> internalProps = properties;
		final String property = internalProps.removeFirst();

		if (internalProps.isEmpty()) {
			action.doProperty(target, property, value);
		} else {
			Object object = getProperty(target, property);
			if (object instanceof Collection) {

				@SuppressWarnings("rawtypes")
				final Iterator items = ((Collection) object).iterator();

				LinkedList<String> clonedProps = null;

				while (items.hasNext()) {

					clonedProps = (LinkedList<String>) internalProps.clone();

					object = items.next();
					if (object != null) {
						setHierarchicalProperty(object, clonedProps, value, action);
					}
				}
			} else if (object != null) {
				setHierarchicalProperty(object, internalProps, value, action);
			}
		}
	}

	public interface PropertyAction {
		void doProperty(Object target, String property, Object value);
	}

	public static final PropertyAction PROPERTY_SETTER = (target, property, value) -> setProperty(target, property, value);

}
