package com.br.util.testesquadra.model.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;


public enum DominioSimNao implements Dominio {

	//@formatter:off
	NAO(0, "Não"),
	SIM(1, "Sim");
	//@formatter:on

	public static final String NOME = "DominioSimNao";

	private int cod;
	private String desc;
	private String longDesc;

	private DominioSimNao(final int cod, final String desc) {
		this.cod = cod;
		this.desc = desc;
		this.longDesc = desc;
	}

	private DominioSimNao(final int cod, final String desc, final String longDesc) {
		this.cod = cod;
		this.desc = desc;
		this.longDesc = longDesc;
	}

	/**
	 * Retorna a instância do domínio a partir de seu código
	 *
	 * @param codigo
	 * @return
	 */
	public static DominioSimNao valueOf(final Integer codigo) {
		for (final DominioSimNao valor : values()) {
			if (valor.getCod() == codigo) {
				return valor;
			}
		}
		return null;

	}

	/**
	 * Verifica se o código informado existe no domínio
	 *
	 * @param codigo
	 * @return
	 */
	public static boolean isValid(final Integer codigo) {
		return valueOf(codigo) != null;
	}

	/**
	 * Número de elementos do dominio
	 *
	 * @return
	 */
	public static Integer getSize() {
		return values().length;
	}

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public Integer getCod() {
		return this.cod;
	}

	@Override
	public String getDesc() {
		return this.desc;
	}

	@Override
	public String getLongDesc() {
		return StringUtils.isBlank(this.longDesc) ? this.desc : this.longDesc;
	}

	@Override
	public String toString() {
		return this.getDesc();
	}

	public static List<SelectItem> getItems() {
		final List<SelectItem> itens = new ArrayList<>();
		for (final DominioSimNao dominio : values()) {
			final SelectItem item = new SelectItem(dominio.getName(), dominio.getDesc());
			itens.add(item);
		}
		return itens;
	}

	public static class Values {
		public static final String NAO = "0";
		public static final String SIM = "1";

	}
}
