package com.br.util.testesquadra.model.dominio;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * Hibernate 3 Parameterized type for mapping a Java 5 Enum. This allows you to avoid the need to define a concrete UserType instance for every enum that you have. Just create a
 * new typedef for each one, giving it a unique type name. Then reference this type name in the property tag.
 *
 * Ex:
 *
 * @Column
 *
 * 		https://community.jboss.org/wiki/Java5EnumUserType
 *
 * @author
 *
 */
public class GenericEnumUserType implements UserType, ParameterizedType {

	private static final String DEFAULT_GET_VALUE_METHOD_NAME = "getCod";
	private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";

	@SuppressWarnings("rawtypes")
	protected Class enumClass;
	@SuppressWarnings("rawtypes")
	private Class valueType;
	private Method getValueMethod;
	private Method valueOfMethod;
	@SuppressWarnings("rawtypes")
	private AbstractSingleColumnStandardBasicType type;
	private int[] sqlTypes;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setParameterValues(final Properties properties) {

		this.getEnumClassName(properties);

		final String getValueMethodName = properties.getProperty("getValueMethod", DEFAULT_GET_VALUE_METHOD_NAME);
		try {
			this.getValueMethod = this.enumClass.getMethod(getValueMethodName, new Class[0]);
			this.valueType = this.getValueMethod.getReturnType();
		} catch (final Exception e) {
			throw new HibernateException("Failed to obtain identifier method", e);
		}

		final TypeResolver tr = new TypeResolver();
		this.type = (AbstractSingleColumnStandardBasicType) tr.basic(this.valueType.getName());
		if (this.type == null) {
			throw new HibernateException("Unsupported identifier type " + this.valueType.getName());
		}

		this.sqlTypes = new int[] { this.type.sqlType() };

		final String valueOfMethodName = properties.getProperty("valueOfMethod", DEFAULT_VALUE_OF_METHOD_NAME);
		try {
			this.valueOfMethod = this.enumClass.getMethod(valueOfMethodName, new Class[] { this.valueType });
		} catch (final Exception e) {
			throw new HibernateException("Failed to obtain valueOf method", e);
		}
	}

	protected void getEnumClassName(final Properties properties) {
		final String enumClassName = properties.getProperty("enumClass");
		try {
			this.enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
		} catch (final ClassNotFoundException cnfe) {
			throw new HibernateException("Enum class not found", cnfe);
		}
	}

	@Override
	public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(final Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(final Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public boolean equals(final Object x, final Object y) throws HibernateException {
		return x == y;
	}

	@Override
	public int hashCode(final Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor owner, final Object arg3) throws HibernateException, SQLException {

		final Object identifier = this.type.get(rs, names[0], owner);
		if (identifier == null) {
			return null;
		}

		try {
			return this.valueOfMethod.invoke(this.enumClass, new Object[] { identifier });
		} catch (final Exception e) {
			throw new HibernateException("Exception while invoking valueOf method '" + this.valueOfMethod.getName() + "' of enumeration class '" + this.enumClass + "'", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SessionImplementor owner) throws HibernateException, SQLException {
		try {
			if (value == null) {
				st.setNull(index, this.type.sqlType());
			} else {
				Object identifier = null;
				if (value.getClass().equals(this.enumClass)) {
					identifier = this.getValueMethod.invoke(value, new Object[0]);
				} else {
					identifier = this.getValueMethod.invoke(this.enumClass.getMethod("valueOf", String.class).invoke(this.enumClass.getEnumConstants()[0], value), new Object[0]);
				}
				this.type.set(st, identifier, index, owner);
			}
		} catch (final Exception e) {
			throw new HibernateException("Exception while invoking identifierMethod '" + this.getValueMethod.getName() + "' of enumeration class '" + this.enumClass + "'", e);
		}
	}

	@Override
	public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
		return original;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class returnedClass() {
		return this.enumClass;
	}

	@Override
	public int[] sqlTypes() {
		return this.sqlTypes;
	}

}