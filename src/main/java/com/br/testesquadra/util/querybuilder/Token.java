package com.br.testesquadra.util.querybuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal representation of JPA QL parser tokens.
 */
class Token {
	private static final Pattern PROP_NAME = Pattern.compile("(?<=\\$\\{)\\w+(?=\\})");

	/**
	 * Type of the token.
	 */
	Type type;

	/**
	 * Value of the token.
	 */
	String value;

	/**
	 * Clause this token is on (select, where, etc.).
	 */
	Type clause;

	/**
	 * If this token is of type VARIABLE, this holds the referenced property name.
	 */
	String propName;

	/**
	 * Creates a new Token object.
	 *
	 * @param type
	 *            Type of the token.
	 * @param value
	 *            Value of the token.
	 * @param clause
	 *            Clause this token is on (select, where, etc.).
	 */
	Token(final Type type, final String value, final Type clause) {
		this.type = type;
		this.value = value;
		this.clause = clause;

		if (type == Type.VARIABLE) {
			final Matcher m = PROP_NAME.matcher(value);

			if (m.find()) {
				this.propName = m.group();
			}
		}
	}

	/**
	 * A String representation for debugging purposes.
	 *
	 * @return A String representation for debugging purposes.
	 */
	@Override
	public String toString() {
		return "[" + this.clause + "] " + this.type + "(" + this.propName + ") \"" + this.value + "\"";
	}

	enum Type {
		/** Literal text. */
		LITERAL,

		/** A '-delimited String. */
		STRING,

		/** The select keyword. */
		SELECT(true),

		/** The from keyword. */
		FROM(true),

		/** The where keyword. */
		WHERE(true),

		/** The group by keyword. */
		GROUP_BY(true),

		/** The having keyword. */
		HAVING(true),

		/** The order by keyword. */
		ORDER_BY(true),

		/** The and operator. */
		AND,

		/** The or operator. */
		OR,

		/** The not operator. */
		NOT,

		/** The open parenthesis "(". */
		OPEN,

		/** The close parenthesis ")". */
		CLOSE,

		/** A variable reference, of the form "${prop}". */
		VARIABLE;

		private final boolean clause;

		/**
		 * Creates a new Type object.
		 */
		private Type() {
			this(false);
		}

		/**
		 * Creates a new Type object.
		 *
		 * @param clause
		 *            Does this token start a new clause?
		 */
		private Type(final boolean clause) {
			this.clause = clause;
		}

		/**
		 * Does this token start a new clause?
		 *
		 * @return Does this token start a new clause?
		 */
		public boolean isClause() {
			return this.clause;
		}
	}
}
