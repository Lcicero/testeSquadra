package com.br.util.testesquadra.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.builder.EqualsBuilder;


@MappedSuperclass
public abstract class BaseEntity<PK extends Serializable> implements BaseModel<PK> {

	private static final long serialVersionUID = 4350933809591115243L;

	@Override
	public int hashCode() {
		if (this.getId() == null) {
			return super.hashCode();
		}
		return this.getId().hashCode();
	}

	@Override
	public boolean equals(final Object object) {

		if (this == object) {
			return true;
		}

		if (object == null) {
			return false;
		}

		if (!(object instanceof BaseEntity)) {
			return object.equals(this.getId());
		}

		final BaseEntity<?> other = (BaseEntity<?>) object;
		final EqualsBuilder equalsBuilder = new EqualsBuilder();

		return equalsBuilder.append(this.getId(), other.getId()).isEquals();

	}

}
