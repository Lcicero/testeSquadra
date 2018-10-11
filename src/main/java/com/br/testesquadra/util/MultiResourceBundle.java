package com.br.testesquadra.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class MultiResourceBundle extends ResourceBundle {

	protected static final Control CONTROL = new MultiResourceBundleControl();
	private Properties properties;

	public MultiResourceBundle() {
		this.setParent(ResourceBundle.getBundle("messages", CONTROL));
	}

	protected MultiResourceBundle(final Properties properties) {
		this.properties = properties;
	}

	@Override
	protected Object handleGetObject(final String key) {
		return this.properties != null ? this.properties.get(key) : this.parent.getObject(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<String> getKeys() {
		return this.properties != null ? (Enumeration<String>) this.properties.propertyNames() : this.parent.getKeys();
	}

	protected static class MultiResourceBundleControl extends Control {

		@Override
		public long getTimeToLive(final String baseName, final Locale locale) {
			return 0;
		}

		@Override
		public boolean needsReload(final String baseName, final Locale locale, final String format, final ClassLoader loader, final ResourceBundle bundle, final long loadTime) {
			return true;
		}

		@Override
		public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {
			final Properties properties = this.load(baseName, loader);
			final String include = properties.getProperty("include");
			if (include != null) {
				for (final String includeBaseName : include.split("\\s*,\\s*")) {
					final Properties includedProperties = this.load(includeBaseName, loader);
					if (includedProperties != null) {
						properties.putAll(includedProperties);
					}
				}
			}
			return new MultiResourceBundle(properties);
		}

		private Properties load(final String baseName, final ClassLoader loader) throws IOException {

			if (loader.getResourceAsStream("/messages/" + baseName + ".properties") != null) {
				final Properties properties = new Properties();
				properties.load(loader.getResourceAsStream("/messages/" + baseName + ".properties"));
				return properties;
			}

			return null;

		}
	}

}