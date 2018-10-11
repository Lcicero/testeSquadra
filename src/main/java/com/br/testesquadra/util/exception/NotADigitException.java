package com.br.testesquadra.util.exception;

/**
 * Throw to indicate that an input character was expected to be a decimal digit, but isn't.
 */
public class NotADigitException extends IllegalArgumentException {

	private static final long serialVersionUID = -8510772293368606656L;

	/**
	 * Creates a new NotADigitException object.
	 *
	 * @param c
	 *            The offending character.
	 */
	public NotADigitException(final char c) {
		super("Não é um dígito válido: " + c);
	}
}
