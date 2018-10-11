package com.br.testesquadra.web.bean.util.controller;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.br.testesquadra.facade.SistemaServiceFacade;
import com.br.testesquadra.model.Sistema;
import com.br.testesquadra.util.WebMessageConstants;
import com.br.testesquadra.web.bean.util.TesteSquadraManterBaseBean;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

@Named(value = "manterSistemaBean")
@ViewScoped
@URLMappings(mappings = { @URLMapping(id = "incluirSistema", pattern = "/sistema/incluir", viewId = "/manterSistema.jsf")
		 })
public class ManterSistemaBean extends TesteSquadraManterBaseBean {

	private static final long serialVersionUID = Long.MAX_VALUE;

	public static final String INCLUIR_Sistema_VIEW = "pretty:incluirSistema";
	public static final String VISUALIZAR_Sistema_VIEW = "pretty:pesquisarSistema";

	private Long id;

	private Sistema Sistema;

	@Inject
	private SistemaServiceFacade sistemaServiceFacadeImpl;

	@PostConstruct
	public void inicializar() {
		this.limpar();
	}
	
	@URLAction(mappingId = "incluirSistema", onPostback = false)
	public void configurarModoInclusao() {
		Sistema = new Sistema();
		super.setModoInclusao();
	}

	public String salvar() {

		this.salvarSistema();

		this.addInfoMessage(WebMessageConstants.MSG_INCLUIR_SUCESSO);
		Sistema = new Sistema();
		return "";

	}

	void salvarSistema() {

		this.sistemaServiceFacadeImpl.cadastrarAtualizarSistema(this.Sistema);

	}

	public String cancelar() {

		this.limpar();

		return PesquisaSistemaBean.HOME_Sistema_VIEW;

	}

	public String visualizar() {
		return VISUALIZAR_Sistema_VIEW;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void carregar() {
		this.Sistema = this.sistemaServiceFacadeImpl.carregarSistemaEager(this.getId());

	}

	@Override
	public void limpar() {
		this.Sistema = new Sistema();

	}

	public Sistema getSistema() {
		return this.Sistema;
	}

	public void setSistema(final Sistema Sistema) {
		this.Sistema = Sistema;
	}

}
