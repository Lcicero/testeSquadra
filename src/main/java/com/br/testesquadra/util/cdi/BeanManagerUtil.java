package com.br.testesquadra.util.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BeanManagerUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(BeanManagerUtil.class);

	private BeanManagerUtil() {
		// Utility Class
	}

	/**
	 * Default JNDI name of the BeanManager
	 */
	public final static String BEAN_MANAGER_JNDI = "java:comp/BeanManager";

	/**
	 * Reference to the BeanManager
	 */
	public static BeanManager getBeanManagerFromJNDI() {

		LOGGER.debug("Getting BeanManager from JNDI");

		try {
			// perform lookup
			final InitialContext initialContext = new InitialContext();
			final Object obj = initialContext.lookup(BEAN_MANAGER_JNDI);

			return (BeanManager) obj;

		} catch (final NamingException e) {
			LOGGER.error("Error getting BeanManager from JNDI", e);
		}

		return null;
	}

}
