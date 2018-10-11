package com.br.util.testesquadra.model;

import java.io.Serializable;

public abstract class Criteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sortField;
	private String sortOrder;

	public String getSortField() {
		return this.sortField;
	}

	public void setSortField(final String sortField) {
		this.sortField = sortField;
	}

	public String getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(final String sortOrder) {
		this.sortOrder = sortOrder;
	}
}
