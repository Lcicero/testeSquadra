package com.br.testesquadra.util.querybuilder;

import java.lang.annotation.Annotation;

/**
 * Exception thrown by QueryBuilder.
 */
public class QueryBuilderException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 5363669999753754712L;

	/**
	 * Creates a new QueryBuilderException object.
	 *
	 * @param type
	 *            Type where the problem happened.
	 * @param propName
	 *            Name of the property where the problem happened.
	 * @param message
	 *            Problem message.
	 */
	public QueryBuilderException(@SuppressWarnings("rawtypes") final Class type, final String propName, final String message) {
		super("Type " + type.getName() + ", property " + propName + ": " + message);
	}

	/**
	 * Creates a new QueryBuilderException that signals that only one of the given annotation types may be present at once in a property.
	 *
	 * @param type
	 *            Type where the problem happened.
	 * @param propName
	 *            Name of the property where the problem happened.
	 * @param annotations
	 *            Annotation types that caused the conflict.
	 */
	public QueryBuilderException(@SuppressWarnings("rawtypes") final Class type, final String propName, final Class<? extends Annotation>[] annotations) {
		this(type, propName, createMessage(annotations));
	}

	/**
	 * Creates a message that says that only one of the given annotation types may be present at once in a property.
	 *
	 * @param annotations
	 *            Annotation types that caused the conflict.
	 *
	 * @return Message indicating the conflict.
	 */
	private static String createMessage(final Class<? extends Annotation>[] annotations) {
		if ((annotations == null) || (annotations.length == 0)) {
			return null;
		}

		final StringBuilder builder = new StringBuilder("Only one of ");
		builder.append('@').append(annotations[0].getName());

		for (int i = 1; i < annotations.length; i++) {
			builder.append(", @").append(annotations[i].getName());
		}

		builder.append(" can be present at once");

		return builder.toString();
	}
}
