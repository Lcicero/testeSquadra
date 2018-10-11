package com.br.testesquadra.util;

import java.util.Properties;

import org.hibernate.HibernateException;

import com.br.util.testesquadra.model.dominio.GenericEnumUserType;


public class SistemaGenericEnumUserType extends GenericEnumUserType {

	public static final String[] PACOTE_DOMINIOS_APP = new String[] { "com.br.util.testesquadra.model.dominio" };

	public static final String ENUM_CLASS_NAME_PARAM = "enumClass";

	@Override
	protected void getEnumClassName(final Properties properties) {

		ClassNotFoundException exception = null;

		for (final String pacote : SistemaGenericEnumUserType.PACOTE_DOMINIOS_APP) {

			final StringBuilder sbPacote = new StringBuilder(pacote);

			final String enumClassName = properties.getProperty(SistemaGenericEnumUserType.ENUM_CLASS_NAME_PARAM);

			try {

				this.enumClass = Class.forName(sbPacote.append(".").append(enumClassName).toString()).asSubclass(Enum.class);
				exception = null;
				break;
			} catch (final ClassNotFoundException cnfe) {
				exception = cnfe;
			}
		}

		if (exception != null) {
			throw new HibernateException("Domínio não encontrado", exception);
		}
	}
}