package com.br.testesquadra.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Utility Class to handle Numbers
 */
public class NumberUtil extends NumberUtils {

	/** Default construct */
	protected NumberUtil() {
		// Utility class
	}

	/**
	 * Sets a BigDecimal'a scale
	 *
	 * @param value
	 *            Value to be scaled
	 * @param mode
	 *            RoundingMode to be used
	 * @param decimalPlaces
	 *            Decimal Places to scale
	 * @return Scaled Bigdecimal
	 */
	public static BigDecimal setScale(final BigDecimal value, final RoundingMode mode, final int decimalPlaces) {
		if (value == null) {
			return null;
		}

		return value.setScale(decimalPlaces, mode);
	}

	/**
	 * Tranforma uma string em um BigDecimal
	 *
	 * @param string
	 *            Passar o valor no formato PT-BR Ex: 150,02
	 *
	 * @return Bigdecimal de duas casas decimais
	 */
	public static BigDecimal toBigDecimal(final String string) throws ParseException {

		if ((string == null) || StringUtils.isEmpty(string)) {
			return BigDecimal.ZERO;
		}

		final NumberFormat nF = NumberFormat.getNumberInstance(new Locale("pt", "BR"));

		return setScale(new BigDecimal(nF.parse(string).doubleValue()), RoundingMode.HALF_EVEN, 2);
	}

	public static int stringToInt(final String string) {

		if ((string == null) || StringUtils.isEmpty(string)) {
			return 0;
		}

		return NumberUtils.createInteger(string);
	}

	/**
	 * Sets a double's scale
	 *
	 * @param value
	 *            Value to be scaled
	 * @param mode
	 *            RoundingMode to be used
	 * @param decimalPlaces
	 *            Decimal Places to scale
	 * @return Scaled double
	 */
	public static double setScale(final Double value, final RoundingMode mode, final int decimalPlaces) {

		if (value == null) {
			return 0d;
		}

		return BigDecimal.valueOf(value).setScale(decimalPlaces, mode).doubleValue();
	}

	public static boolean ehMaiorQueZero(final BigDecimal valor) {
		return (valor != null) && (valor.compareTo(BigDecimal.ZERO) > 0);
	}

	public static boolean ehMenorQueZero(final BigDecimal valor) {
		return (valor != null) && (valor.compareTo(BigDecimal.ZERO) < 0);
	}

	public static boolean ehIgualZero(final BigDecimal valor) {
		return (valor != null) && (valor.compareTo(BigDecimal.ZERO) == 0);
	}

	public static boolean menorOuIgualAZero(final BigDecimal valor) {
		return (valor != null) && (valor.compareTo(BigDecimal.ZERO) <= 0);
	}

	public static boolean saoNumerosIguais(final BigDecimal valor1, final BigDecimal valor2) {
		return valor1.compareTo(valor2) == 0;
	}

	public static BigDecimal stringToBigDecimal(final String valor, final Integer casasDecimais) {

		if (StringUtils.isEmpty(valor)) {
			return null;
		}

		return BigDecimal.valueOf(Long.valueOf(valor).longValue(), casasDecimais);
	}

	public static String NumeroFormatadoString(final double value) {

		final Locale locale = new Locale("pt", "BR");
		final NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
		return fmt.format(value);
	}

}
