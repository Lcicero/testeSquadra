package com.br.testesquadra.util.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.br.testesquadra.util.dao.BaseDAO;
import com.br.util.testesquadra.model.BaseModel;
import com.br.util.testesquadra.model.MethodInvocationCacheUpdateEvent;



@Local
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BaseService<T extends BaseModel> implements Serializable {

	private static final long serialVersionUID = -3207731412436359968L;

	@Inject
	@Any
	private Event<MethodInvocationCacheUpdateEvent> dmlEvent;

	public abstract BaseDAO getDao();

	// Begin Hooks
	public abstract void validar(final T model);

	public abstract void validarPersist(final T model);

	public abstract void validarMerge(final T model);

	public void validarRemove(final T model) {

	};
	// End Hooks

	/**
	 * Find any entity by id
	 *
	 * @param id
	 *            Entity id
	 *
	 * @return Entity from database or null if it doesn't exist
	 */
	@SuppressWarnings("hiding")
	public <T> T findById(final Serializable id) {

		return (T) this.getDao().findById(id);
	}

	public List<T> findAll() {

		return this.getDao().findAll();
	}

	public boolean existsById(final Serializable id) {

		return this.getDao().findById(id) != null;
	}

	

	// Begin Template Methods
	// The container/server has to implement the stubs and skeletons for the
	// Remote interface of the EJB.
	// The stub resides on the client machine and delegates the method call to
	// the skeleton, which resides in the container.
	// The skeleton interprets the parameters sent to it, and delegates the call
	// to the appropriate business method in the EJB.
	// One of the ways a container can implement this skeleton is to extend the
	// EJB class, and have the method call delegate to the superclass.
	// If the business methods are declared final, the container can no longer
	// extend the EJB class to implement the skeleton.
	public void persist(final T model) {

		this.validar(model);
		this.validarPersist(model);
		this.getDao().persist(model);

		this.dmlEvent.fire(new MethodInvocationCacheUpdateEvent(this.getClass().getName()));
	}

	public void merge(final T model) {

		this.validar(model);
		this.validarMerge(model);
		this.getDao().merge(model);

		this.dmlEvent.fire(new MethodInvocationCacheUpdateEvent(this.getClass().getName()));
	}

	public void persistOrMerge(final T model) {

		final boolean isMerge = model.getId() != null;

		this.validar(model);

		if (isMerge) {
			this.validarMerge(model);
		} else {
			this.validarPersist(model);
		}

		this.getDao().persistOrMerge(model);

		if (isMerge) {
			this.dmlEvent.fire(new MethodInvocationCacheUpdateEvent(this.getClass().getName()));
		}
	}

	public void remove(final T model) {

		this.validar(model);
		this.validarRemove(model);
		this.getDao().remove(model);

		this.dmlEvent.fire(new MethodInvocationCacheUpdateEvent(this.getClass().getName()));
	}

	// End Template Methods
}