package com.br.testesquadra.util;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Utility class to handle exceptions
 */
public class ExceptionUtil extends ExceptionUtils {

	private static final int MAX_EXCEPTION_DEPTH = 24;

	/** Default constructor */
	protected ExceptionUtil() {
		// Utility class
	}

	/**
	 * Find one of the Exception Classes that caused this exception, if it exists. If it doesn't exist, return the exception itself.
	 *
	 * @param throwable
	 *            Exception generated
	 * @return exceptionClass generated
	 */
	@SafeVarargs
	public static Throwable findException(final Throwable throwable, final Class<? extends Throwable>... exceptionClasses) {
		Throwable cause = throwable;

		for (final Class<? extends Throwable> c : exceptionClasses) {

			cause = findException(throwable, c);

			if (cause != throwable) {
				return cause;
			}

		}

		return throwable;
	}

	/**
	 * Find the Exception Class that caused this exception, if it exists. If it doesn't exist, return the exception itself.
	 *
	 * @param throwable
	 *            Exception generated
	 * @return exceptionClass generated
	 */
	public static Throwable findException(final Throwable throwable, final Class<? extends Throwable> exceptionClass) {
		Throwable cause = throwable;

		int i = 0;

		do {
			if (cause.getClass().isAssignableFrom(exceptionClass)) {
				return cause;
			}
			i++;
			cause = cause.getCause();
		} while ((i < MAX_EXCEPTION_DEPTH) && (cause != null));

		return throwable;
	}

}
