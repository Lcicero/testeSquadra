package com.br.testesquadra.util.service;

import java.io.Serializable;

import com.br.util.testesquadra.model.BaseModel;


@SuppressWarnings("rawtypes")
public interface IBaseService {

	/**
	 * Find any entity by id
	 *
	 * @param id
	 *            Entity id
	 *
	 * @return Entity from database or null if it doesn't exist
	 */
	<T> T findById(Serializable id);

	void persist(BaseModel model);

	void merge(BaseModel model);

	void saveOrUpdate(BaseModel model);

	void delete(BaseModel model);

}