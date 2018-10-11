package com.br.testesquadra.dao;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Query;

import com.br.testesquadra.criteria.SistemaCriteria;
import com.br.testesquadra.model.Sistema;
import com.br.testesquadra.util.dao.BaseDAO;
import com.br.testesquadra.util.querybuilder.QueryBuilder;



public class SistemaDAO extends BaseDAO<Sistema, Long> {

	public SistemaDAO() {
		super(Sistema.class);
	}

	@SuppressWarnings("unchecked")
	public List<Sistema> findAllByCriteria(final SistemaCriteria criteria) {
		final Query query = new QueryBuilder().createQuery(this.getSession(), criteria);
		return query.list();
	}

	

}
