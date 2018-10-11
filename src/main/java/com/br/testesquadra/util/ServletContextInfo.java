package com.br.testesquadra.util;

public class ServletContextInfo {

	private static String contextPath;

	public static void setContextPath(final String contextPath) {
		ServletContextInfo.contextPath = contextPath;
	}

	public static String getContextPath() {
		return ServletContextInfo.contextPath;
	}

}
