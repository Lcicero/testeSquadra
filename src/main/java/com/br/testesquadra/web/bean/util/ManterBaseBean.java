package com.br.testesquadra.web.bean.util;

import java.io.Serializable;

/**
 * Classe base para beans que implementam a realização de CRUDs
 */
public abstract class ManterBaseBean extends BaseBean {

	private static final long serialVersionUID = -5946111730866579457L;

	public abstract Serializable getId();

	public abstract void limpar();

	private boolean modoVisualizacao;

	private boolean modoInclusao;

	private boolean modoAlteracao;

	private boolean modoExclusao;

	public String getModoDesc() {
		return this.isModoInclusao() ? "Incluir " : this.isModoExclusao() ? "Excluir " : this.isModoAlteracao() ? "Alterar " : "Visualizar ";
	}

	public void setModoVisualizacao() {
		this.modoVisualizacao = true;
	}

	public boolean isModoVisualizacao() {
		return this.modoVisualizacao;
	}

	public void setModoAlteracao() {
		this.modoVisualizacao = false;
		this.modoInclusao = false;
		this.modoAlteracao = true;
		this.modoExclusao = false;
	}

	public boolean isModoAlteracao() {
		return this.modoAlteracao && this.isIdDefinido();
	}

	public void setModoExclusao() {
		this.modoVisualizacao = true;
		this.modoInclusao = false;
		this.modoAlteracao = false;
		this.modoExclusao = true;
	}

	public boolean isModoExclusao() {
		return this.modoExclusao;
	}

	public void setModoInclusao() {
		this.modoVisualizacao = false;
		this.modoInclusao = true;
		this.modoAlteracao = false;
		this.modoExclusao = false;
	}

	public boolean isModoInclusao() {
		return this.modoInclusao && !this.isIdDefinido();
	}

	public boolean isIdDefinido() {
		return this.getId() != null && !"".equals(this.getId());
	}

	public boolean mostraBotaoAlterar() {
		return this.isModoVisualizacao() && !this.isModoExclusao();
	}

	public boolean mostraBotaoSalvarInclusao() {
		return this.isModoInclusao();
	}

	public boolean mostraBotaoSalvarAlteracao() {
		return this.isModoAlteracao();
	}

	public boolean mostraBotaoExcluir() {
		return this.isModoExclusao();
	}

	public boolean mostraBotaoLimpar() {
		return this.isModoInclusao();
	}

	public boolean mostraBotaoCancelarIncluirExcluir() {
		return this.isModoInclusao() || this.isModoExclusao();
	}

	public boolean mostraBotaoCancelarAlterar() {
		return this.isModoAlteracao() && !this.isModoVisualizacao();
	}

	public boolean mostraBotaoNovaPesquisa() {
		return !this.isModoExclusao() && this.isModoVisualizacao();
	}

}
