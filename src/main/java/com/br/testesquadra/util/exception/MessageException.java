package com.br.testesquadra.util.exception;

public class MessageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MessageException() {
	}

	public MessageException(final String message) {
		super(message);
	}

	public MessageException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MessageException(final Throwable cause) {
		super(cause);
	}
}
