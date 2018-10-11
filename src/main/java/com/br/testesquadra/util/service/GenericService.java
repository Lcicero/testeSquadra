package com.br.testesquadra.util.service;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.br.testesquadra.util.dao.BaseDAO;
import com.br.testesquadra.util.dao.GenericDAO;
import com.br.util.testesquadra.model.BaseModel;


@Stateless
@SuppressWarnings("rawtypes")
public class GenericService extends BaseService {

	/**
	 *
	 */
	private static final long serialVersionUID = 7693472048027296791L;

	@Inject
	private GenericDAO dao;

	public GenericService() {

	}

	@SuppressWarnings("unchecked")
	public <T> T findById(final Serializable id, final Class<?> type) {

		return (T) this.dao.findById(id, type);
	}

	@Override
	public BaseDAO getDao() {

		return this.dao;
	}

	@Override
	public void validar(final BaseModel model) {

	}

	@Override
	public void validarPersist(final BaseModel model) {

	}

	@Override
	public void validarMerge(final BaseModel model) {

	}

	@Override
	public void validarRemove(final BaseModel model) {

	}

}