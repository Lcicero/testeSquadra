package com.br.testesquadra.web.bean.util.componentes;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;

public class CustomResourceHandler extends javax.faces.application.ResourceHandlerWrapper {

	private static final String LIBRARY_JS = "js";
	private static final String LIBRARY_CSS = "css";
	private static final String LIBRARY_SPARK = "spark-layout";

	private final ResourceHandler wrapped;

	public CustomResourceHandler(final ResourceHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ResourceHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public Resource createResource(final String resourceName, final String libraryName) {

		final Resource resource = super.createResource(resourceName, libraryName);

		if ((resource != null) && (libraryName != null) && (libraryName.equalsIgnoreCase(CustomResourceHandler.LIBRARY_JS)
				|| libraryName.equalsIgnoreCase(CustomResourceHandler.LIBRARY_CSS) || libraryName.equalsIgnoreCase(CustomResourceHandler.LIBRARY_SPARK))) {
			return new CustomResource(resource);
		} else {
			return resource;
		}

	}

}