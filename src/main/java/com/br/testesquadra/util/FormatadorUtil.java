package com.br.testesquadra.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.text.MaskFormatter;

public class FormatadorUtil {

	public static Locale brasil = new Locale("pt", "BR");
	public static NumberFormat numberFormat = NumberFormat.getNumberInstance(brasil);

	public static String formataCpf(final String cpf) {
		return format("###.###.###-##", cpf);
	}

	public static String formataCnpj(final String cnpj) {
		return format("##.###.###/####-##", cnpj);
	}

	public static String somenteNumeros(final String valor) {

		if (valor == null) {
			return null;
		}

		return valor.replaceAll("\\D", "");
	}

	private static String format(final String pattern, final Object value) {

		if (value == null) {
			return "";
		}

		try {
			final MaskFormatter mask = new MaskFormatter(pattern);
			mask.setValueContainsLiteralCharacters(false);
			return mask.valueToString(value);
		} catch (final ParseException e) {
			return "";
		}
	}

	public static String formataMonetario(final BigDecimal valor) {

		if (valor == null) {
			return "R$ 0,00";
		}

		final DecimalFormat DINHEIRO_REAL = new DecimalFormat("¤ ###,###,##0.00");

		return DINHEIRO_REAL.format(valor);
	}

	public static String formataPercentual(final BigDecimal valor) {

		if (valor == null) {
			return "0.00 %";
		}

		final DecimalFormat PERCENTUAL = new DecimalFormat("##,## %");

		return PERCENTUAL.format(valor);
	}

	public static String formataTelefone(final String telefoneTomador) {
		return format("(##) ####-#####", telefoneTomador);
	}

	public static String formataCep(final String cepPrestador) {
		return format("#####-###", cepPrestador);
	}

	public static String formatarNumeroInteiro(final Long numero) {

		return (numero != null) ? numberFormat.format(numero) : "0";

	}
}
