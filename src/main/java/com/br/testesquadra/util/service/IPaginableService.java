package com.br.testesquadra.util.service;

import java.io.Serializable;
import java.util.List;

import com.br.util.testesquadra.model.BaseModel;
import com.br.util.testesquadra.model.Criteria;


public interface IPaginableService<I, O extends BaseModel<?>> extends Serializable {

	/**
	 * Obtém todos os registros que seram mostrados em tela passando inicio e tamanho da pagina junto ao criteria
	 */
	public abstract List<O> findAllByCriteriaPaginacao(int first, int pageSize, Criteria criteria, String sortField, String sortOrder);

	/**
	 * Obtém o total de registros baseado no criteria
	 */
	public abstract Long countAllByCriteria(Criteria criteria);

}
