package com.br.testesquadra.util.querybuilder;

import static com.br.testesquadra.util.querybuilder.Token.Type.AND;
import static com.br.testesquadra.util.querybuilder.Token.Type.CLOSE;
import static com.br.testesquadra.util.querybuilder.Token.Type.HAVING;
import static com.br.testesquadra.util.querybuilder.Token.Type.LITERAL;
import static com.br.testesquadra.util.querybuilder.Token.Type.NOT;
import static com.br.testesquadra.util.querybuilder.Token.Type.OPEN;
import static com.br.testesquadra.util.querybuilder.Token.Type.OR;
import static com.br.testesquadra.util.querybuilder.Token.Type.SELECT;
import static com.br.testesquadra.util.querybuilder.Token.Type.STRING;
import static com.br.testesquadra.util.querybuilder.Token.Type.VARIABLE;
import static com.br.testesquadra.util.querybuilder.Token.Type.WHERE;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.br.testesquadra.util.UncheckedPropertyUtil;
import com.br.testesquadra.util.jpa.DAOUtil;
import com.br.testesquadra.util.querybuilder.QueryBuilder.CachedMetadata.ParameterLikeType;
import com.br.testesquadra.util.querybuilder.Token.Type;
import com.br.testesquadra.util.type.BeanType;
import com.br.testesquadra.util.type.TypeIntrospector;
import com.br.util.testesquadra.model.BaseModel;



/**
 * Builds queries (and their parameters) out of properly annotated POJOs.
 *
 * @see <a href="package-summary.html#package_description">Package Description</a>
 */
public class QueryBuilder {

	private static final String LIKE_OPERATOR = "like";

	private static final String PARSE_QUERY_EXPR = "('(?:[^']|'')*')|" + "(\\bselect\\b\\s*)|" + "(\\bfrom\\b\\s*)|" + "(\\bwhere\\b\\s*)|" + "(\\bgroup\\s+by\\b\\s*)|"
			+ "(\\bhaving\\b\\s*)|" + "(\\border\\s+by\\b\\s*)|" + "(\\band\\b\\s*)|" + "(\\bor\\b\\s*)|" + "(\\bnot\\b\\s*)|" + "(\\(\\s*)|" + "(\\)\\s*)|"
			+ "(\\$\\{(?:\\w+|\\.\\.\\.)\\}\\s*)";

	private static final String FIND_PARAMETERS_EXPR = "('(?:[^']|'')*')|" + ":(\\w+)\\b";

	private static final Pattern parseQuery = Pattern.compile(QueryBuilder.PARSE_QUERY_EXPR, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	private static final Pattern findParameters = Pattern.compile(QueryBuilder.FIND_PARAMETERS_EXPR, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	/**
	 * Parses a template text into a series of tokens.
	 *
	 * @param s
	 *            Template text.
	 * @param whereExprs
	 *            Available where expressions.
	 * @param havingExprs
	 *            Available having expressions.
	 *
	 * @return Template text split into tokens.
	 */
	private static ArrayList<Token> parseQuery(String s, final Set<String> whereExprs, final Set<String> havingExprs, boolean isCount) {

		s = s.trim();
		final ArrayList<Token> tokens = new ArrayList<>();
		final Matcher m = QueryBuilder.parseQuery.matcher(s);
		int lastPos = 0;
		Type lastType = null;
		Type clause = SELECT;
		Token last = null;

		while (m.find()) {
			if (m.start() > lastPos) {
				final String value = s.substring(lastPos, m.start());

				if (lastType == LITERAL) {
					last.value += value;
				} else {

					if (isCount) {
						last = new Token(LITERAL, " COUNT(*) ", clause);
						isCount = false;
					} else {
						last = new Token(LITERAL, value, clause);
					}
					tokens.add(last);

				}

				lastType = LITERAL;
			}

			for (int i = 1; i <= m.groupCount(); i++) {
				final String value = m.group(i);

				if (value != null) {
					final Type currentType = Type.values()[i];

					if (((currentType == NOT) && (lastType != AND) && (lastType != OR)) || (currentType == STRING)) {
						if (lastType == LITERAL) {
							last.value += value;
						} else {
							last = new Token(LITERAL, value, clause);
							tokens.add(last);
						}

						lastType = LITERAL;
					} else {
						if (currentType.isClause()) {
							clause = currentType;
						}

						lastType = currentType;
						last = new Token(currentType, value, clause);

						if (last.propName != null) {
							if (clause == WHERE) {
								whereExprs.remove(last.propName);
							} else {
								havingExprs.remove(last.propName);
							}
						}

						tokens.add(last);
					}

					break;
				}
			}

			lastPos = m.end();
		}

		if (lastPos < s.length()) {
			tokens.add(new Token(LITERAL, s.substring(lastPos), clause));
		}

		for (int i = 0; i < tokens.size(); i++) {
			final Token token = tokens.get(i);

			if ((token.type == VARIABLE) && (token.propName == null)) {
				if (token.clause == WHERE) {
					i = QueryBuilder.replaceRemaining(tokens, i, whereExprs);
				} else if (token.clause == HAVING) {
					i = QueryBuilder.replaceRemaining(tokens, i, havingExprs);
				}
			}
		}

		return tokens;
	}

	/**
	 * Replaces the token at the given position by the expression composed of the expressions available in the given Set.
	 *
	 * @param tokens
	 *            Parsed template.
	 * @param position
	 *            Token to replace.
	 * @param remaining
	 *            Expressions to use to build the inserted expression.
	 *
	 * @return The position immediately after the replaced content.
	 */
	private static int replaceRemaining(final List<Token> tokens, final int position, final Set<String> remaining) {

		if ((remaining == null) || remaining.isEmpty()) {
			return QueryBuilder.removeVariable(tokens, position);
		} else {
			final Type clause = tokens.get(position).clause;
			final List<Token> temp = new ArrayList<>();

			for (final String prop : remaining) {
				temp.add(new Token(VARIABLE, "${" + prop + "} ", clause));
				temp.add(new Token(AND, "and ", clause));
			}

			temp.remove(temp.size() - 1);
			temp.get(temp.size() - 1).value = temp.get(temp.size() - 1).value.trim() + tokens.get(position).value.substring(tokens.get(position).value.lastIndexOf('}') + 1);
			tokens.remove(position);
			tokens.addAll(position, temp);

			return (position + temp.size()) - 1;
		}
	}

	/**
	 * Removes the token at the given position, possibly cascading to adjacent elements like dangling operators or empty parenthesis.
	 *
	 * @param tokens
	 *            Parsed template.
	 * @param position
	 *            Token to remove.
	 *
	 * @return The position immediately after the removed content.
	 */
	private static int removeVariable(final List<Token> tokens, final int position) {

		final Type clause = tokens.get(position).clause;
		int start = position;
		int end = position;
		Token before = start > 0 ? tokens.get(start - 1) : null;
		Token after = end < (tokens.size() - 1) ? tokens.get(end + 1) : null;

		boolean done = false;

		while (!done) {
			if ((before != null) && (before.type == NOT)) {
				start--;
				before = start > 0 ? tokens.get(start - 1) : null;
			}

			if ((before != null) && (before.type == AND)) {
				start--;
				before = start > 0 ? tokens.get(start - 1) : null;
			} else if ((after != null) && (after.type == AND)) {
				end++;
				after = end < (tokens.size() - 1) ? tokens.get(end + 1) : null;
			} else if ((before != null) && (before.type == OR)) {
				start--;
				before = start > 0 ? tokens.get(start - 1) : null;
			} else if ((after != null) && (after.type == OR)) {
				end++;
				after = end < (tokens.size() - 1) ? tokens.get(end + 1) : null;
			}

			if ((before != null) && (after != null) && (before.type == OPEN) && (after.type == CLOSE)) {
				start--;
				before = start > 0 ? tokens.get(start - 1) : null;
				end++;
				after = end < (tokens.size() - 1) ? tokens.get(end + 1) : null;
				done = false;
			} else {
				done = true;
			}
		}

		if ((before != null) && (before.type == clause) && ((after == null) || after.type.isClause())) {
			start--;
			before = start > 0 ? tokens.get(start - 1) : null;
		}

		tokens.subList(start, end + 1).clear();

		return start - 1;
	}

	/**
	 * Builds the query for a given query object.
	 *
	 * @param queryObject
	 *            Bean that serves as the query template.
	 * @param parametersOut
	 *            Map where parameter values will be put, indexed by their names.
	 *
	 * @return Query text, using the standard JPA named parameter syntax, e.g., {@code :paramName}.
	 *
	 * @throws NullPointerException
	 *             If <code>queryObject</code> is null.
	 * @throws IllegalArgumentException
	 *             If <code>queryObject</code> is not properly annotated.
	 */
	public static String parseQuery(final Object queryObject, final Map<String, Object> parametersOut, final boolean isCount) {

		return QueryBuilder.parseQuery(queryObject, parametersOut, null, null, isCount);

	}

	/**
	 * Builds the query for a given query object, with a custom named parameter syntax.
	 *
	 * <p>
	 * This class uses, by default, the standard JPA syntax for named parameters, e.g. {@code :paramName}. Using {@code paramPrefix} and {@code paramSuffix}, one can use a
	 * different syntax. For example, to use JasperReports parameter syntax {@code $P&#123;paramName&#125;}, one could pass {@code "$P&#123;"} and {@code "&#125;"} as paramPrefix
	 * and paramSuffix, respectively
	 * </p>
	 *
	 * @param queryObject
	 *            Bean that serves as the query template.
	 * @param parametersOut
	 *            Map where parameter values will be put, indexed by their names.
	 * @param paramPrefix
	 *            Prefix to use for named parameters in query text.
	 * @param paramSuffix
	 *            Suffix to use for named parameters in query text.
	 *
	 * @return Query text, using the given prefix and suffix for named parameters.
	 *
	 * @throws NullPointerException
	 *             If <code>queryObject</code> is null.
	 * @throws IllegalArgumentException
	 *             If <code>queryObject</code> is not properly annotated.
	 */
	public static String parseQuery(final Object queryObject, Map<String, Object> parametersOut, String paramPrefix, String paramSuffix, final boolean isCount) {

		if (queryObject == null) {
			throw new NullPointerException("Null queryObject in createQuery()");
		}

		if (parametersOut == null) {
			parametersOut = new HashMap<>();
		}

		final CachedMetadata meta = CachedMetadata.getMetadata(queryObject, isCount);
		final ArrayList<Token> tokens = meta.tokens;
		final HashMap<String, Map<Condition, String>> whereExprs = meta.whereExprs;
		final HashMap<String, Map<Condition, String>> havingExprs = meta.havingExprs;

		final Object bean = queryObject;

		for (int i = 0; i < tokens.size(); i++) {
			final Token token = tokens.get(i);

			if (token.type == VARIABLE) {
				Map<Condition, String> data = null;

				switch (token.clause) {
				case WHERE:
					data = whereExprs.get(token.propName);
					break;

				case HAVING:
					data = havingExprs.get(token.propName);
					break;

				default:
					throw new IllegalArgumentException("Expressions are not supported in the " + token.clause + " clause: " + token.value);
				}

				if (data == null) {
					throw new IllegalArgumentException("Unknown expression in " + token.clause + " clause: " + token.value);
				} else {
					final Object value = UncheckedPropertyUtil.getProperty(bean, token.propName);
					String expr = null;

					for (final Map.Entry<Condition, String> entry : data.entrySet()) {
						if (entry.getKey().eval(value)) {
							expr = entry.getValue();

							if (expr == null) {
								expr = (String) value;
							}
						}
					}

					if (expr == null) {
						i = QueryBuilder.removeVariable(tokens, i);
					} else {
						final String prefix = token.value.substring(0, token.value.indexOf('$'));
						final String suffix = token.value.substring(token.value.lastIndexOf('}') + 1);
						tokens.set(i, new Token(LITERAL, prefix + expr + suffix, token.clause));
					}
				}
			}
		}

		final StringBuilder text = new StringBuilder(tokens.size() * 10);

		for (int i = 0; i < tokens.size(); i++) {
			final Token token = tokens.get(i);
			text.append(token.value);
		}

		final Matcher params = QueryBuilder.findParameters.matcher(text);

		Map<String, Object> customParameters = null;
		if (queryObject instanceof CustomQueryParameters) {
			customParameters = ((CustomQueryParameters) queryObject).emitCustomParameters();
		}

		paramPrefix = paramPrefix == null ? "" : paramPrefix;
		paramSuffix = paramSuffix == null ? "" : paramSuffix;
		final boolean replaceParams = StringUtils.isNotBlank(paramPrefix) || StringUtils.isNotBlank(paramSuffix);
		final StringBuffer result = new StringBuffer(text.length() * 2);

		while (params.find()) {

			final String param = params.group(2);

			if (param == null) {
				continue;
			} else {
				try {
					Object value = null;

					try {
						value = UncheckedPropertyUtil.getProperty(bean, param);
					} catch (final Exception e) {
					}

					if (value != null) {

						if (meta.caseInsensitiveProperties.contains(param)) {
							value = StringUtils.upperCase(value.toString());
						}

						if (meta.propertyLikeTypeMap.containsKey(param)) {
							if (ParameterLikeType.BEGIN_WITH.equals(meta.propertyLikeTypeMap.get(param))) {
								value = DAOUtil.getBeginWithParameter(value.toString());
							} else if (ParameterLikeType.END_WITH.equals(meta.propertyLikeTypeMap.get(param))) {
								value = DAOUtil.getEndWithParameter(value.toString());
							} else if (ParameterLikeType.CONTAIN.equals(meta.propertyLikeTypeMap.get(param))) {
								value = DAOUtil.getLikeParameter(value.toString());
							}
						}
					}

					parametersOut.put(param, value);

				} catch (final Exception e) {
					if ((customParameters != null) && customParameters.containsKey(param)) {
						parametersOut.put(param, customParameters.get(param));
					} else {
						throw new IllegalArgumentException("Missing param " + param + " in query " + text.toString());
					}
				}
			}

			if (replaceParams) {
				params.appendReplacement(result, Matcher.quoteReplacement(paramPrefix + param + paramSuffix));
			}
		}

		params.appendTail(result);

		if ((meta.orderBy != null) && !isCount) {
			result.append(meta.orderBy);
		}

		return result.toString();

	}

	/**
	 * Puts the expression that should be used if the property is empty into the map.
	 *
	 * @param getter
	 *            Property getter method.
	 * @param propName
	 *            Property name.
	 * @param result
	 *            Map to put the expression, keyed by the Condition.
	 *
	 * @throws QueryBuilderException
	 *             If the property is not properly annotated.
	 */
	private static void getEmptyExpression(final Method getter, final String propName, final Map<Condition, String> result) {

		final Class<? extends Annotation> which = QueryBuilder.whichIsPresent(getter, propName, IfEmpty.class, IfTrueOrEmpty.class, IfFalseOrEmpty.class);

		if (which == null) {
			return;
		} else if (getter.getReturnType().isPrimitive()) {
			throw new QueryBuilderException(getter.getDeclaringClass(), propName, "@" + which.getName() + " doesn't apply to primitive properties");
		} else if (which == IfEmpty.class) {
			final String expr = getter.getAnnotation(IfEmpty.class).value();
			result.put(Condition.EMPTY, expr);
		} else if (which == IfTrueOrEmpty.class) {
			QueryBuilder.checkType(getter, propName, which, Boolean.class, Boolean.TYPE);

			final String expr = getter.getAnnotation(IfTrueOrEmpty.class).value();
			result.put(Condition.TRUE_OR_EMPTY, expr);
		} else {
			QueryBuilder.checkType(getter, propName, which, Boolean.class, Boolean.TYPE);

			final String expr = getter.getAnnotation(IfFalseOrEmpty.class).value();
			result.put(Condition.FALSE_OR_EMPTY, expr);
		}
	}

	/**
	 * Puts the expression that should be used if the property is not empty into the map.
	 *
	 * @param getter
	 *            Property getter method.
	 * @param propName
	 *            Property name.
	 * @param result
	 *            Map to put the expression, keyed by the Condition.
	 */
	private static void getNotEmptyExpression(final Method getter, String target, final String propName, final Map<Condition, String> result,
			final Map<String, ParameterLikeType> parameterLikeTypeMap) {

		final Class<? extends Annotation> which = QueryBuilder.whichIsPresent(getter, propName, Literal.class, IfNotEmpty.class, Equal.class, NotEqual.class, Greater.class,
				GreaterEqual.class, Less.class, LessEqual.class, In.class, NotIn.class, Like.class, BeginWith.class, EndWith.class, Contain.class);

		String expr = null;

		if (which == null) {

			return;

		} else if (which == Literal.class) {

			QueryBuilder.checkType(getter, propName, which, String.class);
			expr = null;

		} else if (which == IfNotEmpty.class) {

			expr = getter.getAnnotation(IfNotEmpty.class).value();

		} else if (which == Equal.class) {

			expr = QueryBuilder.createOperatorExpression(getter.getAnnotation(Equal.class).value(), target, propName, "=", null);

		} else if (which == NotEqual.class) {

			expr = QueryBuilder.createOperatorExpression(getter.getAnnotation(NotEqual.class).value(), target, propName, "<>", null);

		} else if (which == Greater.class) {

			expr = QueryBuilder.createOperatorExpression(getter.getAnnotation(Greater.class).value(), target, propName, ">", null);

		} else if (which == GreaterEqual.class) {

			expr = QueryBuilder.createOperatorExpression(getter.getAnnotation(GreaterEqual.class).value(), target, propName, ">=", null);

		} else if (which == Less.class) {

			expr = QueryBuilder.createOperatorExpression(getter.getAnnotation(Less.class).value(), target, propName, "<", null);

		} else if (which == LessEqual.class) {

			expr = QueryBuilder.createOperatorExpression(getter.getAnnotation(LessEqual.class).value(), target, propName, "<=", null);

		} else if (which == In.class) {

			expr = QueryBuilder.createInOperatorExpression(getter.getAnnotation(In.class).value(), target, propName, "in");

		} else if (which == NotIn.class) {

			expr = QueryBuilder.createInOperatorExpression(getter.getAnnotation(NotIn.class).value(), target, propName, "not in");

		} else if (which == Like.class) {

			final Like annotation = getter.getAnnotation(Like.class);
			parameterLikeTypeMap.put(propName, ParameterLikeType.LIKE);

			target = StringUtils.isBlank(annotation.value()) ? target : annotation.value();
			expr = QueryBuilder.createOperatorExpression(annotation.value(), target, propName, QueryBuilder.LIKE_OPERATOR, annotation.caseSensitive());

		} else if (which == BeginWith.class) {

			final BeginWith annotation = getter.getAnnotation(BeginWith.class);
			parameterLikeTypeMap.put(propName, ParameterLikeType.BEGIN_WITH);

			target = StringUtils.isBlank(annotation.value()) ? target : annotation.value();
			expr = QueryBuilder.createOperatorExpression(annotation.value(), target, propName, QueryBuilder.LIKE_OPERATOR, annotation.caseSensitive());

		} else if (which == EndWith.class) {

			final EndWith annotation = getter.getAnnotation(EndWith.class);
			parameterLikeTypeMap.put(propName, ParameterLikeType.END_WITH);

			target = StringUtils.isBlank(annotation.value()) ? target : annotation.value();
			expr = QueryBuilder.createOperatorExpression(annotation.value(), target, propName, QueryBuilder.LIKE_OPERATOR, annotation.caseSensitive());

		} else if (which == Contain.class) {

			final Contain annotation = getter.getAnnotation(Contain.class);
			parameterLikeTypeMap.put(propName, ParameterLikeType.CONTAIN);

			target = StringUtils.isBlank(annotation.value()) ? target : annotation.value();
			expr = QueryBuilder.createOperatorExpression(annotation.value(), target, propName, QueryBuilder.LIKE_OPERATOR, annotation.caseSensitive());

		}

		result.put(Condition.NOT_EMPTY, expr);
	}

	/**
	 * Creates an expression of the form "prop op :param".
	 *
	 * @param target
	 *            Optional target property. If null propertyName will be used.
	 * @param propName
	 *            Property name.
	 * @param operator
	 *            Operator to use.
	 *
	 * @return Resulting expression.
	 */
	private static String createOperatorExpression(final String getterValue, String target, final String propName, final String operator, final Boolean isCaseSensitive) {

		if (StringUtils.isNotBlank(getterValue)) {
			target = getterValue;
		}

		if ((isCaseSensitive != null) && !isCaseSensitive) {
			return (StringUtils.isBlank(target) ? "UPPER(:" + propName + ")" : "UPPER(" + target + ")") + " " + operator + " UPPER(:" + propName + ")";
		} else {
			return (StringUtils.isBlank(target) ? propName : target) + " " + operator + " :" + propName;
		}

	}

	/**
	 * Creates an expression of the form "prop op (:param)".
	 *
	 * @param target
	 *            Optional target property. If null propertyName will be used.
	 * @param propName
	 *            Property name.
	 * @param operator
	 *            Operator to use.
	 *
	 * @return Resulting expression.
	 */
	private static String createInOperatorExpression(final String getterValue, String target, final String propName, final String operator) {

		if (StringUtils.isNotBlank(getterValue)) {
			target = getterValue;
		}
		return (StringUtils.isBlank(target) ? propName : target) + " " + operator + " (:" + propName + ")";
	}

	/**
	 * Puts the expression that should be used if the property value is true into the map.
	 *
	 * @param getter
	 *            Property getter method.
	 * @param propName
	 *            Property name.
	 * @param result
	 *            Map to put the expression, keyed by the Condition.
	 */
	private static void getTrueExpression(final Method getter, final String propName, final Map<Condition, String> result) {

		final Class<? extends Annotation> which = QueryBuilder.whichIsPresent(getter, propName, IfTrue.class, IfTrueOrEmpty.class);

		if (which == null) {
			return;
		} else if (which == IfTrueOrEmpty.class) {
			QueryBuilder.checkType(getter, propName, which, Boolean.class, Boolean.TYPE);

			final String expr = getter.getAnnotation(IfTrueOrEmpty.class).value();
			result.put(Condition.TRUE_OR_EMPTY, expr);
		} else {
			QueryBuilder.checkType(getter, propName, which, Boolean.class, Boolean.TYPE);

			final String expr = getter.getAnnotation(IfTrue.class).value();
			result.put(Condition.TRUE, expr);
		}
	}

	/**
	 * Checks that a property is of one of the expected types.
	 *
	 * @param getter
	 *            Property getter.
	 * @param propName
	 *            Property name.
	 * @param which
	 *            Annotation (for error message purposes).
	 *
	 * @throws QueryBuilderException
	 *             If the property is not properly annotated.
	 */
	private static void checkType(final Method getter, final String propName, final Class<? extends Annotation> which, @SuppressWarnings("rawtypes") final Class... targetType) {

		if ((targetType == null) || (targetType.length == 0)) {
			return;
		}

		for (@SuppressWarnings("rawtypes")
		final Class type : targetType) {
			if (type == getter.getReturnType()) {
				return;
			}
		}

		final StringBuilder msg = new StringBuilder("@");
		msg.append(which.getName());
		msg.append(" applies only to properties of type ");
		msg.append(targetType[0].getName());

		for (int i = 1; i < (targetType.length - 1); i++) {
			msg.append(", ").append(targetType[i].getName());
		}

		msg.append(" or ").append(targetType[targetType.length - 1].getName());

		throw new QueryBuilderException(getter.getDeclaringClass(), propName, msg.toString());
	}

	/**
	 * Puts the expression that should be used if the property value is false into the map.
	 *
	 * @param getter
	 *            Property getter method.
	 * @param propName
	 *            Property name.
	 * @param result
	 *            Map to put the expression, keyed by the Condition.
	 */
	private static void getFalseExpression(final Method getter, final String propName, final Map<Condition, String> result) {

		final Class<? extends Annotation> which = QueryBuilder.whichIsPresent(getter, propName, IfFalse.class, IfFalseOrEmpty.class);

		if (which == null) {
			return;
		} else if (which == IfFalseOrEmpty.class) {
			QueryBuilder.checkType(getter, propName, which, Boolean.class, Boolean.TYPE);

			final String expr = getter.getAnnotation(IfFalseOrEmpty.class).value();
			result.put(Condition.FALSE_OR_EMPTY, expr);
		} else {
			QueryBuilder.checkType(getter, propName, which, Boolean.class, Boolean.TYPE);

			final String expr = getter.getAnnotation(IfFalse.class).value();
			result.put(Condition.FALSE, expr);
		}
	}

	/**
	 * Checks which of the mutually exclusive annotations is present in a method.
	 *
	 * @param getter
	 *            Method to check.
	 * @param propName
	 *            Property name.
	 *
	 * @return Which of the annotations was present, possibly null.
	 *
	 * @throws QueryBuilderException
	 *             If more than one of the given annotations is present.
	 */
	@SafeVarargs
	private static Class<? extends Annotation> whichIsPresent(final Method getter, final String propName, final Class<? extends Annotation>... types) {

		Class<? extends Annotation> result = null;

		for (final Class<? extends Annotation> type : types) {
			if (getter.isAnnotationPresent(type)) {
				if (result == null) {
					result = type;
				} else {
					throw new QueryBuilderException(getter.getDeclaringClass(), propName, types);
				}
			}
		}

		return result;
	}

	/**
	 * Creates a typed JPA query and binds the appropriate parameters into it.
	 *
	 * @param em
	 *            EntityManager to use to create the query.
	 * @param queryObject
	 *            Template bean.
	 *
	 * @return Corresponding query, with all necessary parameters bound.
	 */
	/*
	 * public <T> TypedQuery<T> createQuery(EntityManager em, Class<T> type, Object queryObject) { Map<String, Object> parametersOut = new HashMap<String, Object>();
	 *
	 * String text = parseQuery(queryObject, parametersOut);
	 *
	 * TypedQuery<T> query = em.createQuery(text, type);
	 *
	 * processParameters(parametersOut, query);
	 *
	 * return query; }
	 */
	/**
	 * Creates a HIBERNATE query and binds the appropriate parameters into it.
	 *
	 * @param session
	 *            Hibernate Session to use to create the query.
	 * @param queryObject
	 *            Template bean.
	 *
	 * @return Corresponding query, with all necessary parameters bound.
	 */
	public Query createQuery(final Session session, final Object queryObject) {

		final Map<String, Object> parametersOut = new HashMap<>();

		final String text = QueryBuilder.parseQuery(queryObject, parametersOut, false);

		final Query query = session.createQuery(text);
		this.processParameters(parametersOut, query);

		return query;
	}

	/**
	 * Creates a HIBERNATE query and binds the appropriate parameters into it.
	 *
	 * @param session
	 *            Hibernate Session to use to create the query.
	 * @param queryObject
	 *            Template bean.
	 *
	 * @return Corresponding query, with all necessary parameters bound.
	 */
	public Query createCountQuery(final Session session, final Object queryObject) {

		final Map<String, Object> parametersOut = new HashMap<>();

		final String text = QueryBuilder.parseQuery(queryObject, parametersOut, true);

		final Query query = session.createQuery(text.replaceAll("(?i)fetch", ""));
		this.processParameters(parametersOut, query);

		return query;
	}

	private void processParameters(final Map<String, Object> parametersOut, final Query query) {

		for (final Map.Entry<String, Object> entry : parametersOut.entrySet()) {

			if (entry.getValue() instanceof BaseModel) {
				query.setParameter(entry.getKey(), ((BaseModel<?>) entry.getValue()).getId());
			} else if (entry.getValue() instanceof Object[]) {
				query.setParameterList(entry.getKey(), (Object[]) entry.getValue());
			} else {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
	}

	static class CachedMetadata {

		enum ParameterLikeType {
			BEGIN_WITH, END_WITH, CONTAIN, LIKE
		}

		private static final ConcurrentHashMap<Class<?>, CachedMetadata> cache = new ConcurrentHashMap<>();

		String query;

		String mainAlias;

		ArrayList<String> caseInsensitiveProperties;

		Map<String, ParameterLikeType> propertyLikeTypeMap;

		ArrayList<Token> tokens;

		HashMap<String, Map<Condition, String>> whereExprs;

		HashMap<String, Map<Condition, String>> havingExprs;

		BeanType bean;

		String orderBy;

		static final CachedMetadata getMetadata(final Object queryObject, final boolean isCount) {

			final Class<?> type = queryObject.getClass();
			CachedMetadata meta = CachedMetadata.cache.get(type);
			// if (meta == null) {
			meta = new CachedMetadata(type, isCount);
			// ache.put(type, meta);
			// }
			return new CachedMetadata(meta, queryObject, isCount);
		}

		private CachedMetadata(final CachedMetadata source, final Object queryObject, final boolean isCount) {
			this.whereExprs = source.whereExprs;
			this.havingExprs = source.havingExprs;
			this.propertyLikeTypeMap = source.propertyLikeTypeMap;
			this.caseInsensitiveProperties = source.caseInsensitiveProperties;

			if (source.query == null) {
				this.query = ((DynamicQuery) queryObject).query();
				this.tokens = QueryBuilder.parseQuery(this.query, new TreeSet<>(this.whereExprs.keySet()), new TreeSet<>(this.havingExprs.keySet()), isCount);

			} else {
				this.query = source.query;
				this.tokens = new ArrayList<>(source.tokens);
			}

			if (queryObject != null) {
				final String sortField = (String) UncheckedPropertyUtil.getProperty(queryObject, "sortField");
				final String sortOrder = (String) UncheckedPropertyUtil.getProperty(queryObject, "sortOrder");

				if (sortField != null) {
					this.orderBy = " order by " + source.mainAlias + "." + sortField + " " + sortOrder;
				}
			}
		}

		private CachedMetadata(final Class<?> type, final boolean isCount) {

			this.propertyLikeTypeMap = new HashMap<>();
			this.caseInsensitiveProperties = new ArrayList<>();

			this.whereExprs = new HashMap<>();
			this.havingExprs = new HashMap<>();

			try {
				this.bean = (BeanType) TypeIntrospector.resolve(type);
			} catch (final IntrospectionException ex) {
				throw new IllegalArgumentException("Can't introspect " + type, ex);
			}

			StaticQuery staticQuery = null;
			if (DynamicQuery.class.isAssignableFrom(type)) {
				this.query = null;
				this.tokens = null;
			} else {
				staticQuery = type.getAnnotation(StaticQuery.class);

				if (staticQuery == null) {
					throw new IllegalArgumentException("Type " + type.getName() + " neither implements DynamicQuery nor is annotated with @StaticQuery");
				}

				this.query = staticQuery.value();
			}

			if (staticQuery != null) {
				this.tokens = QueryBuilder.parseQuery(this.query, new TreeSet<>(this.whereExprs.keySet()), new TreeSet<>(this.havingExprs.keySet()), isCount);

				if ((this.tokens.size() >= 3) && this.tokens.get(3).type.equals(Type.LITERAL)) {
					final String[] subTokens = this.tokens.get(3).value.split(" ");
					if ((subTokens.length > 1) && subTokens[1].equals("as")) {
						this.mainAlias = subTokens[2];
					}
				}
			}

			for (final Map.Entry<String, BeanType.Property> entry : this.bean.properties.entrySet()) {
				final String name = entry.getKey();
				String target = name;
				if (StringUtils.isNotBlank(this.mainAlias)) {
					target = this.mainAlias + "." + name;
				}

				final BeanType.Property prop = entry.getValue();
				final Method getter = prop.getter;

				if (getter != null) {

					final Map<Condition, String> data = new HashMap<>();

					if (this.getterIsCaseInsensitive(getter, name)) {
						this.caseInsensitiveProperties.add(name);
					}

					QueryBuilder.getNotEmptyExpression(getter, target, name, data, this.propertyLikeTypeMap);
					QueryBuilder.getEmptyExpression(getter, name, data);
					QueryBuilder.getTrueExpression(getter, name, data);
					QueryBuilder.getFalseExpression(getter, name, data);
					if (data.size() > 0) {
						if (getter.getAnnotation(Having.class) != null) {
							this.havingExprs.put(name, data);
						} else {
							this.whereExprs.put(name, data);
						}
					}
				}
			}

			if (staticQuery != null) {
				this.tokens = QueryBuilder.parseQuery(this.query, new TreeSet<>(this.whereExprs.keySet()), new TreeSet<>(this.havingExprs.keySet()), isCount);
			}

		}

		public boolean getterIsCaseInsensitive(final Method getter, final String propName) {

			final Class<? extends Annotation> which = QueryBuilder.whichIsPresent(getter, propName, BeginWith.class, EndWith.class, Contain.class, Like.class);

			if (which == null) {
				return false;
			}

			if (which == BeginWith.class) {
				return !getter.getAnnotation(BeginWith.class).caseSensitive();
			} else if (which == EndWith.class) {
				return !getter.getAnnotation(EndWith.class).caseSensitive();
			} else if (which == Contain.class) {
				return !getter.getAnnotation(Contain.class).caseSensitive();
			} else if (which == Like.class) {
				return !getter.getAnnotation(Like.class).caseSensitive();
			}

			return false;

		}

	}
}
