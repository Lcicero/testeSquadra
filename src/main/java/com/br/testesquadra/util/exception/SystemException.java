package com.br.testesquadra.util.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

/**
 * Runtime Exception thrown whenever a system exception occurs
 */
@ApplicationException(rollback = true, inherited = true)
public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 2848782740732727359L;

	private List<String> messages = new ArrayList<>();

	public SystemException() {
		super();
	}

	public SystemException(final List<String> messages) {
		super();
		this.messages = messages;
	}

	public SystemException(final String message) {
		super(message);
		this.messages.add(message);
	}

	public SystemException(final Throwable cause) {
		super(cause.getMessage(), cause);
		if (cause instanceof SystemException) {
			this.messages.addAll(((SystemException) cause).getMessages());
		} else {
			this.messages.add(cause.getMessage());
		}
	}

	public SystemException(final String message, final Throwable cause) {
		super(message, cause);
		this.messages.add(message);
	}

	public List<String> getMessages() {
		return this.messages;
	}

}
