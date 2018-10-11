package com.br.testesquadra.util.exception;

public class MailException extends RuntimeException {

	private static final long serialVersionUID = -6662182835427831252L;

	public MailException() {
	}

	public MailException(final String message) {
		super(message);
	}

	public MailException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MailException(final Throwable cause) {
		super(cause);
	}
}
