package com.br.testesquadra.util.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.br.testesquadra.util.exception.BusinessException;
import com.br.testesquadra.util.querybuilder.QueryBuilder;
import com.br.util.testesquadra.model.BaseModel;
import com.br.util.testesquadra.model.BaseVersionedEntity;
import com.br.util.testesquadra.model.dominio.DominioSimNao;



public abstract class BaseDAO<T extends BaseModel<PK>, PK extends Serializable> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8073254562269518507L;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAO.class);

	 @PersistenceContext(unitName = "default")
	private EntityManager entityManager;

	private final Class<T> type;

	public BaseDAO() {
		this(null);
	}

	protected BaseDAO(final Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(final Order... orders) {

		final Criteria criteria = this.getSession().createCriteria(this.type);

		if (orders != null) {
			for (final Order order : orders) {
				criteria.addOrder(order);
			}
		}

		return criteria.list();

	}

	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(final Criterion... criterion) {

		final Criteria crit = this.getSession().createCriteria(this.type);
		for (final Criterion c : criterion) {
			crit.add(c);
		}
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(final String orderField, final Criterion... criterion) {

		final Criteria crit = this.getSession().createCriteria(this.type);
		for (final Criterion c : criterion) {
			crit.add(c);
		}
		crit.addOrder(Order.asc(orderField));
		return crit.list();
	}

	public List<T> findByExample(final T example) {

		return this.findByCriteria(Example.create(example).enableLike(MatchMode.ANYWHERE).ignoreCase());
	}

	public List<T> findByExample(final T exampleInstance, final String[] excludeProperty) {

		final Example example = Example.create(exampleInstance);
		for (final String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		return this.findByCriteria(example);
	}

	public List<T> findByField(final String fieldOnTable, final Object value) {

		return this.findByCriteria(Restrictions.eq(fieldOnTable, value).ignoreCase());
	}

	@SuppressWarnings("unchecked")
	public List<T> findByField(final String[] fieldOnTable, final Object[] value) {

		final Criteria c = this.getSession().createCriteria(this.type);

		for (int i = 0; i < fieldOnTable.length; i++) {

			if (value[i] instanceof String) {
				c.add(Restrictions.eq(fieldOnTable[i], String.valueOf(value[i])));
			} else if (value[i] instanceof Date) {
				c.add(Restrictions.eq(fieldOnTable[i], value[i]));
			} else {
				c.add(Restrictions.eq(fieldOnTable[i], value[i]));
			}
		}
		return c.list();
	}

	public List<T> findByFieldBetween(final String field, final Object valueInit, final Object valueEnd) {

		return this.findByCriteria(Restrictions.between(field, valueInit, valueEnd));
	}

	@SuppressWarnings("unchecked")
	public List<T> findByFieldInList(final String field, final List<Object> values) {

		final Criteria criteria = this.getSession().createCriteria(this.type);
		criteria.add(Restrictions.in(field, values));
		return criteria.list();
	}

	public List<T> findByFieldLike(final String fieldOnTable, final String value) {

		return this.findByCriteria(Restrictions.like(fieldOnTable, value, MatchMode.ANYWHERE).ignoreCase());
	}

	@SuppressWarnings("unchecked")
	public List<T> findByFieldLike(final String[] fieldOnTable, final Object[] value) {

		final Criteria c = this.getSession().createCriteria(this.type);

		for (int i = 0; i < fieldOnTable.length; i++) {

			if (value[i] instanceof String) {
				c.add(Restrictions.like(fieldOnTable[i], String.valueOf(value[i]), MatchMode.ANYWHERE).ignoreCase());
			} else if (value[i] instanceof Date) {
				c.add(Restrictions.eq(fieldOnTable[i], value[i]));
			} else {
				c.add(Restrictions.like(fieldOnTable[i], value[i]));
			}
		}
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByFieldLikeOrdered(final String fieldOnTable, final String value, final String orderBy) {

		final Criteria criteria = this.getSession().createCriteria(this.type).add(Restrictions.like(fieldOnTable, value, MatchMode.ANYWHERE).ignoreCase());
		criteria.addOrder(Order.asc(orderBy));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByFieldOrdered(final String fieldOnTable, final Object value, final String orderBy) {

		final Criteria criteria = this.getSession().createCriteria(this.type).add(Restrictions.eq(fieldOnTable, value));
		criteria.addOrder(Order.asc(orderBy));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public T loadByField(final String fieldName, final Object value) {

		final Criteria criteria = this.getSession().createCriteria(this.type).add(Restrictions.eq(fieldName, value));
		final Object obj = criteria.uniqueResult();
		return (obj != null) ? (T) obj : null;
	}

	@SuppressWarnings("unchecked")
	public T loadByField(final String[] fieldOnTable, final Object[] expression) {

		final Criteria c = this.getSession().createCriteria(this.type);

		for (int i = 0; i < fieldOnTable.length; i++) {

			if (expression[i] instanceof String) {
				c.add(Restrictions.eq(fieldOnTable[i], String.valueOf(expression[i])));
			} else if (expression[i] instanceof Date) {
				c.add(Restrictions.eq(fieldOnTable[i], expression[i]));
			} else {
				c.add(Restrictions.eq(fieldOnTable[i], expression[i]));
			}
		}
		return (T) c.uniqueResult();
	}

	/**
	 * Conulta uma entidade no banco de dados utilizando a chave primária do mesmo.
	 *
	 * @param pk
	 *            chave primária.
	 * @return objeto retornado do banco de dados.
	 */
	@SuppressWarnings("unchecked")
	public T findById(final PK pk) {

		BaseDAO.LOGGER.debug("FindById: (#{}) {}", pk, this.type.getSimpleName());

		final T entityLoaded = (T) this.getSession().get(this.type, pk);

		return entityLoaded;
	}

	public void merge(final T entity) {

		BaseDAO.LOGGER.debug("Merge: (#{}) {}", entity.getId(), entity.getClass().getSimpleName());
		// entity.setDataAtualizacao(new Date());
		this.getSession().merge(entity);
		this.checkConstraint(entity);
	}

	public void persist(final T entity) {

		BaseDAO.LOGGER.debug("Persist: {}", entity.getClass().getSimpleName());
		// entity.setDataCriacao(new Date());
		// entity.setDataAtualizacao(new Date());
		this.getSession().persist(entity);
		this.checkConstraint(entity);
	}

	public void persistOrMerge(final T entity) {

		if (entity.getId() == null) {
			this.persist(entity);
		} else {
			this.merge(entity);
		}

	}

	public void checkConstraint(final T entity) {

		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		final Set<ConstraintViolation<T>> contraintViolations = validator.validate(entity);

		if (contraintViolations != null) {
			contraintViolations.forEach(violation -> {
				throw new BusinessException("O campo " + violation.getPropertyPath() + " " + violation.getMessage());
			});
		}
	}

	/**
	 * Atualiza a entidade passada por parâmetro com as informações do banco de dados.
	 *
	 * @param entity
	 *            Entidade a ser aualizada.
	 * @return Entidade com os dados do banco de dados.
	 */
	public T refresh(final T entity) {

		BaseDAO.LOGGER.debug("Refresh: (#{}) {}", entity.getId(), entity.getClass().getSimpleName());
		this.getSession().refresh(entity);
		return entity;

	}

	public T detach(final T entity) {

		BaseDAO.LOGGER.debug("Evict: (#{}) {}", entity.getId(), entity.getClass().getSimpleName());
		this.getSession().evict(entity);
		return entity;

	}

	public void remove(final T entity) {

		BaseDAO.LOGGER.debug("Remove: (#{}) {}", entity.getId(), entity.getClass().getSimpleName());

		final Session session = this.getSession();
		final Object entityToRemove = session.get(entity.getClass(), entity.getId());

		if (entityToRemove != null) {
			session.delete(entityToRemove);
		}

	}

	public void flush() {

		BaseDAO.LOGGER.debug("Flushing statements...");
		this.getSession().flush();
	}

	public void clear() {

		BaseDAO.LOGGER.debug("Clearing session...");
		this.getSession().clear();
	}

	protected Session getSession() {

		final Session session = this.getEntityManager().unwrap(Session.class);
		final Filter filter = session.enableFilter(BaseVersionedEntity.FLAG_EXCLUIDO);
		filter.setParameter(BaseVersionedEntity.FLAG_EXCLUIDO, DominioSimNao.NAO.getCod());

		return session;
	}

	protected EntityManager getEntityManager() {

		if (this.entityManager == null) {
			try {
				final Context initCtx = new InitialContext();
				this.entityManager = (javax.persistence.EntityManager) initCtx.lookup("java:comp/env/persistence/default");

			} catch (final NamingException e) {
				e.printStackTrace();
			}
		}

		return this.entityManager;
	}

	public Class<T> getType() {

		return this.type;
	}

	@SuppressWarnings("unchecked")
	public List<T> findAllByCriteriaPaginacao(final int first, final int pageSize, final com.br.util.testesquadra.model.Criteria criteria) {

		final Query query = new QueryBuilder().createQuery(this.getSession(), criteria);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);

		return query.list();
	}

	public Long countAllByCriteria(final com.br.util.testesquadra.model.Criteria criteria) {

		final Query query = new QueryBuilder().createCountQuery(this.getSession(), criteria);

		return (Long) query.uniqueResult();
	}

	
}
