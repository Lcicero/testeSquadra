package com.br.testesquadra.web.bean.util;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.ocpsoft.pretty.PrettyContext;


public abstract class TesteSquadraBaseBean extends BaseBean {

	private static final long serialVersionUID = -1612882899516565110L;

	protected static final Integer TEMPO_EXPIRACAO_COOKIE = 60;
	protected static final String ULTIMA_PAGINA_ACESSADA = "ULTIMA_PAGINA_ACESSADA";
	

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

	

	/**
	 * Remove O cookies da requisição
	 *
	 * @param request
	 *            Request
	 * @param response
	 *            Response
	 */
	public void removerCookieUltimaPaginaAcessada(final HttpServletRequest request, final HttpServletResponse response) {

		final Cookie[] cookies = request.getCookies();
		if ((cookies == null) || (cookies.length == 0)) {
			return;
		}

		// Remove todos os cookies do response
		for (final Cookie cookie : cookies) {

			if (cookie.getName().equals(TesteSquadraBaseBean.ULTIMA_PAGINA_ACESSADA)) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

	/**
	 * Mantendo o estado da última página acessada pelo usuário
	 *
	 * @param req
	 *            Request
	 * @param resp
	 *            Response
	 */
	protected void manterEstadoUltimaPaginaAcessada(final HttpServletRequest request, final HttpServletResponse response) {

		if (request.getRequestURI().contains("login")) {
			return;
		}

		final Cookie ultimaPaginaAcessada = new Cookie(TesteSquadraBaseBean.ULTIMA_PAGINA_ACESSADA, PrettyContext.getCurrentInstance().getRequestURL().toURL());
		ultimaPaginaAcessada.setMaxAge(TesteSquadraBaseBean.TEMPO_EXPIRACAO_COOKIE);

		response.addCookie(ultimaPaginaAcessada);
	}

	

}
