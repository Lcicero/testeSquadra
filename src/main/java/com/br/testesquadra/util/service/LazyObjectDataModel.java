package com.br.testesquadra.util.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.br.testesquadra.util.exception.BusinessException;
import com.br.util.testesquadra.model.BaseModel;
import com.br.util.testesquadra.model.Criteria;


public class LazyObjectDataModel<I, O extends BaseModel<?>> extends LazyDataModel<O> {

	private static final String NENHUM_REGISTRO_ENCONTRADO_UTILIZANDO_OS_FILTROS_INFORMADOS = "Nenhum registro encontrado utilizando os filtros informados";

	private static final long serialVersionUID = 1L;

	private Criteria criteria;
	private IPaginableService<I, O> paginableService;
	
	private List<O> datasource;
	
	private String jsCaseEmpty;

	public LazyObjectDataModel() {
		super();
	}

	public LazyObjectDataModel(final IPaginableService<I, O> paginableService, final Criteria criteria) {
		this.paginableService = paginableService;
		this.criteria = criteria;
		this.datasource = new ArrayList<>();
	}

	public LazyObjectDataModel(final IPaginableService<I, O> paginableService, final Criteria criteria, final String jsCaseEmpty) {
		this.paginableService = paginableService;
		this.criteria = criteria;
		this.jsCaseEmpty = jsCaseEmpty;
		this.datasource = new ArrayList<>();
	}

	@Override
	public List<O> load(int first, final int pageSize, final String sortField, final SortOrder sortOrder, final Map<String, Object> filters) {

		if (this.paginableService == null) {
			return new ArrayList<>();
		}

		if ((filters != null) && !filters.isEmpty()) {

			for (final Map.Entry<String, Object> entry : filters.entrySet()) {

				try {

					final Field field = this.criteria.getClass().getDeclaredField(entry.getKey());

					if (field == null) {
						throw new BusinessException("Campo " + entry.getKey() + " não encontrado");
					}

					field.setAccessible(true);

					field.set(this.criteria, entry.getValue());

				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}

		final int dataSize = this.paginableService.countAllByCriteria(this.criteria).intValue();
		this.setRowCount(dataSize);

		if ((dataSize <= first) && (first != 0) && (dataSize != 0)) {
			first = 0;
		}

		final String order = this.getSortOrder(sortOrder);
		this.criteria.setSortField(sortField);
		this.criteria.setSortOrder(order);

		this.datasource = this.paginableService.findAllByCriteriaPaginacao(first, pageSize, this.criteria, sortField, order);

		if (((this.datasource == null) || this.datasource.isEmpty()) && StringUtils.isNotBlank(this.jsCaseEmpty)) {
			RequestContext.getCurrentInstance().execute(this.jsCaseEmpty);
		}

		if (this.datasource.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(NENHUM_REGISTRO_ENCONTRADO_UTILIZANDO_OS_FILTROS_INFORMADOS, NENHUM_REGISTRO_ENCONTRADO_UTILIZANDO_OS_FILTROS_INFORMADOS));
		}

		return this.datasource;
	}

	@Override
	public O getRowData(final String rowKey) {

		final Long id = Long.parseLong(rowKey);

		for (final O t : this.datasource) {
			if ((t.getId() != null) && t.getId().equals(id)) {
				return t;
			}
		}
		return null;
	}

	@Override
	public O getRowKey(final O o) {
		return o;
	}

	private String getSortOrder(final SortOrder sortOrder) {

		if (sortOrder == null) {
			return StringUtils.EMPTY;
		}

		switch (sortOrder) {
		case ASCENDING:
			return "ASC";
		case DESCENDING:
			return "DESC";
		default:
			return StringUtils.EMPTY;
		}
	}

	public boolean isEmpty() {
		return (this.datasource == null) || this.datasource.isEmpty();
	}

	public List<O> getDatasource() {
		return datasource;
	}

	public void setDatasource(List<O> datasource) {
		this.datasource = datasource;
	}
	
	

}
