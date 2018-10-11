package com.br.testesquadra.util.jpa;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.br.testesquadra.util.CollectionUtil;


public final class DAOUtil {

	private static final String LIKE_WILDCARD = "%";

	private DAOUtil() {
		// Utility class
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSingleResult(final Query query) {

		try {
			final List<T> list = query.getResultList();

			if (CollectionUtils.isNotEmpty(list)) {
				return CollectionUtil.getFirst(list);
			}
		} catch (final NoResultException e) {

		}
		return null;
	}

	public static void setParameterMap(final Query query, final Map<String, Object> parameters) {
		for (final Entry<String, Object> parameter : parameters.entrySet()) {
			query.setParameter(parameter.getKey(), parameter.getValue());
		}
	}

	public static String getLikeParameter(final String value) {
		if (StringUtils.isEmpty(value)) {
			return LIKE_WILDCARD;
		}

		final StringBuilder valueBuilder = new StringBuilder();

		valueBuilder.append(LIKE_WILDCARD);
		valueBuilder.append(value.replaceAll(LIKE_WILDCARD, StringUtils.EMPTY));
		valueBuilder.append(LIKE_WILDCARD);

		return valueBuilder.toString();
	}

	public static String getBeginWithParameter(final String value) {
		if (StringUtils.isEmpty(value)) {
			return LIKE_WILDCARD;
		}

		final StringBuilder valueBuilder = new StringBuilder();

		valueBuilder.append(value.replaceAll(LIKE_WILDCARD, StringUtils.EMPTY));
		valueBuilder.append(LIKE_WILDCARD);

		return valueBuilder.toString();
	}

	public static String getEndWithParameter(final String value) {
		if (StringUtils.isEmpty(value)) {
			return LIKE_WILDCARD;
		}

		final StringBuilder valueBuilder = new StringBuilder();

		valueBuilder.append(LIKE_WILDCARD);
		valueBuilder.append(value.replaceAll(LIKE_WILDCARD, StringUtils.EMPTY));

		return valueBuilder.toString();
	}

}
