package com.br.testesquadra.util.exception;

public class ResultNotCachedException extends RuntimeException {

	/**
	* 
	*/
	private static final long serialVersionUID = 3269565226949186762L;

	public ResultNotCachedException() {
	}

	public ResultNotCachedException(final String message) {
		super(message);
	}

	public ResultNotCachedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ResultNotCachedException(final Throwable cause) {
		super(cause);
	}

}
