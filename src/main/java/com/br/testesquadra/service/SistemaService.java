package com.br.testesquadra.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.criterion.Order;

import com.br.testesquadra.criteria.SistemaCriteria;
import com.br.testesquadra.dao.SistemaDAO;
import com.br.testesquadra.model.Sistema;
import com.br.testesquadra.util.service.PaginableService;
import com.br.util.testesquadra.model.Criteria;


@Stateless
public class SistemaService extends PaginableService<SistemaCriteria, Sistema>{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private SistemaDAO sistemaDAO;
	
	public List<Sistema> findAllByCriteria(final SistemaCriteria criteria) {
		if (criteria != null) {
			final List<Sistema> resultado = this.getDao().findAllByCriteria(criteria);
			return resultado;
		} else {
			throw new IllegalArgumentException("criteria==null");
		}
	}
	
	@Override
	public Long countAllByCriteria(Criteria criteria) {
		
		if (criteria != null) {
			return this.getDao().countAllByCriteria(criteria);
		} else {
			throw new IllegalArgumentException("criteria==null");
		}
		
	}

	@Override
	public SistemaDAO getDao() {
		return this.sistemaDAO;
	}

	@Override
	public void validar(Sistema arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validarMerge(Sistema arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validarPersist(Sistema Sistema) {
		
	}
	

}
