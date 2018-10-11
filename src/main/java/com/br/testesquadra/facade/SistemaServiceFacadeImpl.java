package com.br.testesquadra.facade;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.br.testesquadra.criteria.SistemaCriteria;
import com.br.testesquadra.model.Sistema;
import com.br.testesquadra.service.SistemaService;
import com.br.util.testesquadra.model.Criteria;


@Stateless
public class SistemaServiceFacadeImpl implements SistemaServiceFacade {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private SistemaService sistemaService;

	@Override
	public Long countAllByCriteria(Criteria arg0) {
		return this.sistemaService.countAllByCriteria(arg0);
	}

	@Override
	public List<Sistema> findAllByCriteriaPaginacao(int first, int pageSize, Criteria criteria, String sortField, String sortOrder) {
		return this.sistemaService.findAllByCriteriaPaginacao(first, pageSize, criteria, sortField, sortOrder);
	}

	@Override
	public void cadastrarAtualizarSistema(Sistema Sistema) {
		this.sistemaService.persistOrMerge(Sistema);
	}

	@Override
	public Sistema carregarSistema(Long id) {
		return this.sistemaService.findById(id);
	}

	@Override
	public Sistema carregarSistemaEager(Long id) {
		return null;
	}

	@Override
	public Sistema findOneById(Long pk) {
		return null;
	}

}
