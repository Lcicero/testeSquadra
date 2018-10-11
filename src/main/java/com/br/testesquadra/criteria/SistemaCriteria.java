package com.br.testesquadra.criteria;

import com.br.testesquadra.util.querybuilder.Contain;
import com.br.testesquadra.util.querybuilder.StaticQuery;
import com.br.util.testesquadra.model.Criteria;

@StaticQuery("select sistema from Sistema as sistema where ${...} order by sistema.descricao")
public class SistemaCriteria extends Criteria {

	private static final long serialVersionUID = 1L;

	private String descricao;
	private String email;
	private String sigla;

	@Contain(caseSensitive = false, value = "sistema.descricao")
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(final String name) {
		this.descricao = name;
	}

	@Contain(caseSensitive = false, value = "descricao.email")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String description) {
		this.email = description;
	}

	@Contain(caseSensitive = false, value = "descricao.sigla")
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(final String value) {
		this.sigla = value;
	}

}
