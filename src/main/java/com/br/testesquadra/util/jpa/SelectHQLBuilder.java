package com.br.testesquadra.util.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectHQLBuilder {
	public static final Logger LOGGER = LoggerFactory.getLogger(SelectHQLBuilder.class);
	private final StringBuilder strQuery;
	private final Map<String, Object> mapParameters = new HashMap<>();

	public SelectHQLBuilder() {
		this.strQuery = new StringBuilder();
	}

	public SelectHQLBuilder append(final String statement) {
		this.strQuery.append(statement);
		return this;
	}

	public SelectHQLBuilder appendFilter(final String statement, final Object... objects) {
		if (ArrayUtils.isNotEmpty(objects) && !ArrayUtils.contains(objects, null)) {
			this.strQuery.append(statement);
			this.buildNamedParam(statement, objects);
		}
		return this;
	}

	public <T> SelectHQLBuilder appendFilter(final String statement, final Supplier<T> t) {
		if (this.resolve(t).isPresent()) {
			this.strQuery.append(statement);
			this.buildNamedParam(statement, t);
		}
		return this;
	}

	private void buildNamedParam(final String statement, final Object... objects) {
		final Pattern PATTERN = Pattern.compile(".(:([a-zA-Z0-9]+)+)");
		final Matcher matcher = PATTERN.matcher(statement);
		int count = 0;
		while (matcher.find()) {
			try {
				final Object object = objects[count];
				String namedParam = matcher.group(1);
				namedParam = namedParam.replace(":", "");
				this.mapParameters.put(namedParam, object);
				count++;
				// System.out.println("Parametro e valor: " + matcher.group(1) + " " + object);
			} catch (final ArrayIndexOutOfBoundsException e) {
				LOGGER.error("Quantidade de parâmetros em condição não é a mesma passada por parâmetro.", e);
				throw e;
			}
		}
	}

	private <T> Optional<T> resolve(final Supplier<T> resolver) {
		try {
			final T result = resolver.get();
			return Optional.ofNullable(result);
		} catch (final NullPointerException e) {
			return Optional.empty();
		}
	}

	public Query prepare(final Session session) {
		final Query query = session.createQuery(this.strQuery.toString());
		if (!this.mapParameters.isEmpty()) {
			this.mapParameters.forEach((name, param) -> {
				query.setParameter(name, param);
			});
		}

		return query;
	}

	public static void main(final String[] args) {
		final SelectHQLBuilder selectHQLBuilder = new SelectHQLBuilder();

		selectHQLBuilder.appendFilter(" AND classe.id = :param1 AND :param2 AND :param3 AND :param4", 5, 6, 98, "A");

	}

}
