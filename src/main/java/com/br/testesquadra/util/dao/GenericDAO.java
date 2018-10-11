package com.br.testesquadra.util.dao;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.br.util.testesquadra.model.BaseModel;


@ApplicationScoped
public class GenericDAO extends BaseDAO<BaseModel<Serializable>, Serializable> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4292556556288354177L;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAO.class);

	public GenericDAO() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <T> T findById(final Serializable id, final Class<T> type) {

		T entity = null;

		GenericDAO.LOGGER.debug("FindById: (#{id}) {}", id, type.getSimpleName());

		try {
			entity = (T) this.getSession().load(type, id);
		} catch (final NoResultException exp) {
			GenericDAO.LOGGER.debug("Object {}(#{}) not found", id, type.getSimpleName());
		}

		return entity;

	}

}
