package com.br.testesquadra.util.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

/**
 * Runtime Exception thrown whenever a business exception occurs Please note that this exception WILL cause a transaction rollback
 */
@ApplicationException(rollback = true, inherited = false)
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 6548298590042044557L;

	private String[] params;
	private final List<Serializable> messages = new ArrayList<>();

	public BusinessException() {
		super();
	}

	public BusinessException(final String message) {
		super(message);
		this.messages.add(message);
	}

	public BusinessException(final String message, final String... params) {
		super();
		this.messages.add(message);
		this.params = params;
	}

	public BusinessException(final List<? extends Serializable> messages) {
		super();
		this.messages.addAll(messages);
	}

	public BusinessException(final Throwable cause) {
		super(cause);
		this.messages.add(cause.getMessage());
	}

	public BusinessException(final String message, final Throwable cause) {
		super(message, cause);
		this.messages.add(message);
	}

	public List<Serializable> getMessages() {
		return this.messages;
	}

	public String[] getParams() {
		return this.params;
	}

	public void setParams(final String[] params) {
		this.params = params;
	}

}
