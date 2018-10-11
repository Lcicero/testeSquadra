package com.br.testesquadra.web.bean.util;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Classe base para beans que implementam a realização de CRUDs
 */
public abstract class TesteSquadraManterBaseBean extends ManterBaseBean {

	private static final long serialVersionUID = 1L;
	
	public boolean validaPesquisa(final Object classe) {

		if (classe == null) {
			return false;
		}

		for (final Method method : classe.getClass().getMethods()) {

			method.setAccessible(true);

			try {

				if ((method.getParameterCount() != 0) || !method.getName().contains("get") || method.getName().contains("getClass")) {
					continue;
				}

				final Object valor = method.invoke(classe);

				if (valor != null) {
					return true;
				}

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return false;

	}
}