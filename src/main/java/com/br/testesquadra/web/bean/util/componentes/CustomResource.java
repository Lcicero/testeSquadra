package com.br.testesquadra.web.bean.util.componentes;

import java.net.URLEncoder;

import javax.faces.application.Resource;


public class CustomResource extends javax.faces.application.ResourceWrapper {

	private final javax.faces.application.Resource resource;

	private String revision = "";


	public CustomResource(final Resource resource) {
		this.resource = resource;
	}

	@Override
	public Resource getWrapped() {
		return this.resource;
	}

	@Override
	public String getRequestPath() {
		String requestPath = this.resource.getRequestPath();

		// get current revision
		final String revision = this.getRevision();

		if (requestPath.contains("?")) {
			requestPath = requestPath + "&rv=" + revision;
		} else {
			requestPath = requestPath + "?rv=" + revision;
		}

		return requestPath;
	}

	private String getRevision() {

		try {

		

			this.revision = "1";

			this.revision = URLEncoder.encode(this.revision, "UTF-8");

		} catch (final Exception e) {
			System.out.println("#### /META-INF/MANIFEST.MF NÃO ENCONTRADO! IMPOSSÍVEL EXTRAIR A VERSAO DO SISTEMA! ####");
		}

		return this.revision;
	}

	@Override
	public String getContentType() {
		return this.getWrapped().getContentType();
	}

	@Override
	public String getLibraryName() {
		return this.getWrapped().getLibraryName();
	}

	@Override
	public String getResourceName() {
		return this.getWrapped().getResourceName();
	}

	@Override
	public void setContentType(final String contentType) {
		this.getWrapped().setContentType(contentType);
	}

	@Override
	public void setLibraryName(final String libraryName) {
		this.getWrapped().setLibraryName(libraryName);
	}

	@Override
	public void setResourceName(final String resourceName) {
		this.getWrapped().setResourceName(resourceName);
	}

	@Override
	public String toString() {
		return this.getWrapped().toString();
	}

}