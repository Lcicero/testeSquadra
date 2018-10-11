package com.br.testesquadra.web.bean.util.controller;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.br.testesquadra.criteria.SistemaCriteria;
import com.br.testesquadra.facade.SistemaServiceFacade;
import com.br.testesquadra.model.Sistema;
import com.br.testesquadra.util.WebMessageConstants;
import com.br.testesquadra.util.service.LazyObjectDataModel;
import com.br.testesquadra.web.bean.util.TesteSquadraBaseBean;
import com.ocpsoft.pretty.faces.annotation.URLMapping;


@ViewScoped
@Named(value = "pesquisaSistemaBean")
@URLMapping(id = "pesquisarSistema", pattern = "/sistema/pesquisaistema", viewId = "/pesquisaSistema.jsf")
public class PesquisaSistemaBean extends TesteSquadraBaseBean { // /resources/pages/cadastro/area/pesquisarArea.jsf

	private static final long serialVersionUID = Long.MIN_VALUE;

	public static final String HOME_Sistema_VIEW = "pretty:pesquisarSistema";

	@Inject
	private ManterSistemaBean sistemaBean;

	@Inject
	private SistemaServiceFacade SistemaService;

	private Sistema sistema;


	private SistemaCriteria sistemaCriteria;
	
	private LazyObjectDataModel<SistemaCriteria, Sistema> resultadoPesquisa;


	@PostConstruct
	public void construir() {
		this.limpar();
	}

	public void limpar() {
		this.limparCriteria();
	}

	public void limparCriteria() {
		this.sistemaCriteria = new SistemaCriteria();
	}

	public void pesquisarSistema() {

		if (!super.validaPesquisa(this.sistemaCriteria)) {
			super.addWarningMessage(WebMessageConstants.INFORME_UM_FILTRO);
			return;
		}

		this.resultadoPesquisa = new LazyObjectDataModel<>(this.SistemaService, this.sistemaCriteria);

	}

	public String visualizar() {

		this.sistemaBean.setId(this.sistema.getId());
		return ManterSistemaBean.VISUALIZAR_Sistema_VIEW;
	}
	

	public String irCadastroSistemaPreliminar() {
		return ManterSistemaBean.INCLUIR_Sistema_VIEW;
	}

	public LazyObjectDataModel<SistemaCriteria, Sistema> getResultadoPesquisa() {
		return this.resultadoPesquisa;
	}

	public void setResultadoPesquisa(final LazyObjectDataModel<SistemaCriteria, Sistema> resultadoPesquisa) {
		this.resultadoPesquisa = resultadoPesquisa;
	}

	public SistemaCriteria getSistemaCriteria() {
		return this.sistemaCriteria;
	}

	public void setSistemaCriteria(final SistemaCriteria SistemaCriteria) {
		this.sistemaCriteria = SistemaCriteria;
	}

	public void setSistemaBean(final ManterSistemaBean SistemaBean) {
		this.sistemaBean = SistemaBean;
	}

	public Sistema getSistema() {
		return this.sistema;
	}

	public void setSistema(final Sistema Sistema) {
		this.sistema = Sistema;
	}

}
