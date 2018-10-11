package com.br.testesquadra.util.service;

import java.util.List;

import com.br.util.testesquadra.model.BaseModel;
import com.br.util.testesquadra.model.Criteria;


public abstract class PaginableService<I, O extends BaseModel<?>> extends BaseService<O> implements IPaginableService<I, O> {

	/**
	 *
	 */
	private static final long serialVersionUID = -8976756272246115691L;

	@Override
	@SuppressWarnings("unchecked")
	public List<O> findAllByCriteriaPaginacao(final int first, final int pageSize, final Criteria criteria, final String sortField, final String sortOrder) {

		criteria.setSortField(sortField);
		criteria.setSortOrder(sortOrder);

		return this.getDao().findAllByCriteriaPaginacao(first, pageSize, criteria);
	}

	@Override
	public Long countAllByCriteria(final Criteria criteria) {

		return this.getDao().countAllByCriteria(criteria);
	}

}
