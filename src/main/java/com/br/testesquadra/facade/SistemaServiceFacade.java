package com.br.testesquadra.facade;

import java.util.List;

import javax.ejb.Local;

import com.br.testesquadra.criteria.SistemaCriteria;
import com.br.testesquadra.model.Sistema;
import com.br.testesquadra.util.service.IPaginableService;


@Local
public interface SistemaServiceFacade extends IPaginableService<SistemaCriteria, Sistema> {

	//public List<Sistema> pesquisarSistema(SistemaCriteria SistemaCriteria);

	public Sistema carregarSistema(Long id);

	public Sistema carregarSistemaEager(Long id);

	public void cadastrarAtualizarSistema(Sistema Sistema);

	public Sistema findOneById(Long pk);

}
