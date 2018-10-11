package com.br.testesquadra.util.exception;

import java.io.Serializable;
import java.util.List;

import javax.ejb.ApplicationException;

/**
 * Runtime Exception thrown whenever a business exception occurs Please note that this exception WILL cause a transaction rollback
 */
@ApplicationException(rollback = true)
public class BusinessErrorException extends BusinessException {

	private static final long serialVersionUID = 6588798590942044757L;

	public BusinessErrorException() {
		super();
	}

	public BusinessErrorException(final List<? extends Serializable> messages) {
		super(messages);
	}

	public BusinessErrorException(final String message) {
		super(message);
	}

	public BusinessErrorException(final Throwable cause) {
		super(cause);
	}

	public BusinessErrorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public BusinessErrorException(final String bundleName, final String errorCode) {
		super(bundleName, errorCode);
	}

}
