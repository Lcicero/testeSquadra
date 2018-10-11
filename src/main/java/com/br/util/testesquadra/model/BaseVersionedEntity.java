package com.br.util.testesquadra.model;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;

import com.br.util.testesquadra.model.dominio.DominioSimNao;


@MappedSuperclass
@FilterDef(name = BaseVersionedEntity.FLAG_EXCLUIDO, parameters = @ParamDef(name = "excluido", type = "int"))
@Filter(name = BaseVersionedEntity.FLAG_EXCLUIDO, condition = "excluido = :excluido")
public abstract class BaseVersionedEntity<PK extends Serializable> extends BaseEntity<PK> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2771594503906447467L;

	public static final String FLAG_EXCLUIDO = "excluido";

	@Version
	@Column(nullable = false)
	private Long versao;

	@Basic(optional = false)
	@NotNull
	@Column(name = "EXCLUIDO", nullable = false)
	@Type(type = DominioSimNao.NOME)
	private DominioSimNao excluido;

	@PrePersist
	public void prePersist() {

		this.excluido = DominioSimNao.NAO;
		this.stringFieldToUpperCase();
	}

	@PreUpdate
	public void preUpdate() {

		this.stringFieldToUpperCase();
	}

	public boolean isRegistroExcluido() {

		if (this.excluido == null) {
			return false;
		}

		return this.excluido.equals(DominioSimNao.SIM) ? true : false;
	}

	private void stringFieldToUpperCase() {

		if (this.getClass().isAnnotationPresent(NotUpperCase.class)) {
			return;
		}

		try {
			for (final Field f : this.getClass().getDeclaredFields()) {

				f.setAccessible(true);

				if (f.isAnnotationPresent(NotUpperCase.class)) {
					continue;
				}

				if (f.getType().equals(String.class)) {
					if (f.get(this) != null) {
						f.set(this, ((String) f.get(this)).toUpperCase());
					}
				}
			}
		} catch (final Exception e) {
			// não gera excessão caso não consiga converter o campo para uppercase
		}
	}

}
