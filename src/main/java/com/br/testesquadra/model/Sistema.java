package com.br.testesquadra.model;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.br.util.testesquadra.model.BaseVersionedEntity;

@Entity
@Table(name = "Sistema")
public class Sistema extends BaseVersionedEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "pk_id_sistema", nullable = false)
	private Long idSistema;

	@NotNull
	@Column(name = "DESCRICAO", length = 100)
	private String descricao;

	@NotNull
	@Column(name = "SIGLA", length = 100)
	private String sigla;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "url", length = 100)
	private String url;
	
	@Column(name = "status", length = 50)
	private String status;

	@Override
	public Long getId() {
		return this.idSistema;
	}

	@Override
	public void setId(final Long id) {
		this.idSistema = id;
	}

	public Long getIdSistema() {
		return idSistema;
	}

	public void setIdSistema(Long idSistema) {
		this.idSistema = idSistema;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
