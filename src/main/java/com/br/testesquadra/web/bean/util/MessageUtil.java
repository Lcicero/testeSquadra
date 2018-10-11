package com.br.testesquadra.web.bean.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.br.testesquadra.util.MultiResourceBundle;

public class MessageUtil {

	private static ResourceBundle bundle;

	public static String getMessageString(final String key, final Object... arguments) {
		String result;
		try {
			result = MessageFormat.format(getBundle().getString(key), arguments);
		} catch (final MissingResourceException e) {
			result = key;
		}
		return result;
	}

	private synchronized static ResourceBundle getBundle() {

		if (bundle == null) {
			bundle = new MultiResourceBundle();
		}

		return bundle;

	}
}
