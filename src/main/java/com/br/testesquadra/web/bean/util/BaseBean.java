package com.br.testesquadra.web.bean.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;


public abstract class BaseBean implements Serializable {

	private static final long serialVersionUID = -3060139889871472326L;

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseBean.class);

	static {
		Locale.setDefault(new Locale("pt", "br"));
	}

	protected void addWarningMessage(final String message, final Object... params) {
		final String textMessage = MessageUtil.getMessageString(message, params);
		this.getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, textMessage, textMessage));
	}

	protected void addErrorMessage(final String message, final Object... params) {
		final String textMessage = MessageUtil.getMessageString(message, params);
		this.getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, textMessage, textMessage));
	}

	protected void addInfoMessage(final String message, final Object... params) {
		final String textMessage = MessageUtil.getMessageString(message, params);
		this.getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, textMessage, textMessage));
	}

	protected String getViewId(final String id) {

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("pretty:");
		stringBuilder.append(id);

		return stringBuilder.toString();

	}

	protected HttpSession getHttpSession() {

		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

	}

	protected FacesContext getFacesContext() {

		return FacesContext.getCurrentInstance();

	}

	protected HttpServletRequest getHttpServletRequest() {

		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

	}

	protected HttpServletResponse getHttpServletResponse() {

		return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

	}

	protected ExternalContext getExternalContext() {

		final FacesContext context = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = context.getExternalContext();

		return externalContext;

	}

	protected void invalidateSession() {

		BaseBean.LOGGER.debug("Efetuando o logout do usuário");

		if (this.getHttpSession() != null) {
			this.getHttpSession().invalidate();
		}

	}

	protected Flash getFlash() {

		return this.getFacesContext().getExternalContext().getFlash();

	}

	protected PrettyConfig getPrettyConfig() {

		final PrettyContext prettyContext = PrettyContext.getCurrentInstance(this.getFacesContext());
		final PrettyConfig prettyConfig = prettyContext.getConfig();

		return prettyConfig;

	}

	protected String getUrlByMappingId(final String id, final Object... params) {

		final UrlMapping mapping = this.getPrettyConfig().getMappingById(id);
		return mapping.getPatternParser().getMappedURL(params).toURL();

	}

	public void doRedirect(final FacesContext context, final String redirectPage, final boolean prefixWithContext) throws FacesException {

		final ExternalContext externalContext = context.getExternalContext();

		try {

			if ((context.getPartialViewContext().isAjaxRequest() || context.getPartialViewContext().isPartialRequest()) && (context.getResponseWriter() == null)
					&& (context.getRenderKit() == null)) {

				final ServletResponse response = (ServletResponse) externalContext.getResponse();
				final ServletRequest request = (ServletRequest) externalContext.getRequest();

				final RenderKitFactory factory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
				final RenderKit renderKit = factory.getRenderKit(context, context.getApplication().getViewHandler().calculateRenderKitId(context));
				final ResponseWriter responseWriter = renderKit.createResponseWriter(response.getWriter(), null, request.getCharacterEncoding());
				context.setResponseWriter(responseWriter);

			}

			if (prefixWithContext) {
				externalContext.redirect(externalContext.getRequestContextPath() + (redirectPage != null ? redirectPage : StringUtils.EMPTY));
			} else if (redirectPage != null) {
				externalContext.redirect(redirectPage);
			}

		} catch (final IOException e) {
			BaseBean.LOGGER.error("IOException - Should not happen, check logs", e);
		}

	}

	public void doRedirect(final FacesContext context, final String redirectPage) throws FacesException {
		this.doRedirect(context, redirectPage, true);
	}

	public boolean isProjectStageDevelopment() {
		final FacesContext ctx = FacesContext.getCurrentInstance();
		final String projectStage = ctx.getExternalContext().getInitParameter("javax.faces.PROJECT_STAGE");
		return projectStage.equals("Development");
	}

	/**
	 * Remove todos os cookies da requisição
	 *
	 * @param request
	 *            Request
	 * @param response
	 *            Response
	 */
	public void removeAllCookie(final HttpServletRequest request, final HttpServletResponse response) {

		final Cookie[] cookies = request.getCookies();
		if ((cookies == null) || (cookies.length == 0)) {
			return;
		}

		// Remove todos os cookies do response
		for (final Cookie cookie : cookies) {
			cookie.setValue("");
			cookie.setPath("/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

}
