package com.br.testesquadra.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

/**
 * Classe utilitária para cálculo de juros compostos
 *
 * @author leandro.ferreira
 */
public class CurrencyUtil {

	/**
	 * Calcula juros compostos pro-rata die (períofo fracionado) levando em conta uma taxa de juros MENSAL aplicada a um intervalo de datas
	 *
	 * @param valorOriginal
	 * @param taxaDeJurosAM
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static BigDecimal calculaJurosCompostosProRataDie(final BigDecimal valorOriginal, final BigDecimal taxaDeJurosAM, final Date dataInicio, final Date dataFim) {
		return CurrencyUtil.calculaJurosCompostosProRataDie(valorOriginal, taxaDeJurosAM, dataInicio, dataFim, false);
	}

	/**
	 * Calcula juros compostos pro-rata die (períofo fracionado) levando em conta uma taxa de juros MENSAL aplicada a um intervalo de datas
	 *
	 * @param valorOriginal
	 * @param taxaDeJurosAM
	 * @param dataInicio
	 * @param dataFim
	 * @param somaAoValorOriginal
	 * @return
	 */
	public static BigDecimal calculaJurosCompostosProRataDie(final BigDecimal valorOriginal, final BigDecimal taxaDeJurosAM, final Date dataInicio, final Date dataFim,
			final boolean somaAoValorOriginal) {

		double meses = DateUtil.diferencaEmMeses(dataInicio, dataFim);

		final LocalDate dataInicioLD = DateUtil.asLocalDate(dataInicio);
		final double propInicio = (double) ((dataInicioLD.lengthOfMonth() - dataInicioLD.getDayOfMonth()) + 1) / (double) dataInicioLD.lengthOfMonth();

		final LocalDate dataFimLD = DateUtil.asLocalDate(dataFim);
		final double propFim = (double) (dataFimLD.getDayOfMonth() - 1) / (double) dataFimLD.lengthOfMonth();

		meses = Double.valueOf(String.format(Locale.US, "%.4f", (meses + propInicio + propFim) - 1));

		// Caso a data final menor que data inicial, não faz o cálculo
		// por considerar que não está vencido
		if (meses < 0) {
			meses = 0;
		}

		return CurrencyUtil.calculaJurosCompostos(valorOriginal, taxaDeJurosAM, meses, somaAoValorOriginal);
	}

	/**
	 * Calcula juros compostos levando em conta uma taxa de juros por PERÍODO FIXO (dia, mês, ano) aplicada a um número de períodos específicos
	 *
	 * @param valorOriginal
	 * @param taxaDeJurosPorPeriodo
	 * @param periodos
	 * @return
	 */
	public static BigDecimal calculaJurosCompostos(final BigDecimal valorOriginal, final BigDecimal taxaDeJurosPorPeriodo, final double periodos) {
		return CurrencyUtil.calculaJurosCompostos(valorOriginal, taxaDeJurosPorPeriodo, periodos, false);
	}

	/**
	 * Calcula juros compostos levando em conta uma taxa de juros por PERÍODO FIXO (dia, mês, ano) aplicada a um número de períodos específicos
	 *
	 * @param valorOriginal
	 * @param taxaDeJurosAM
	 * @param meses
	 * @param somaAoValorOriginal
	 * @return
	 */
	public static BigDecimal calculaJurosCompostos(final BigDecimal valorOriginal, final BigDecimal taxaDeJurosPorPeriodo, final double periodos,
			final boolean somaAoValorOriginal) {
		final double juros = Math.pow(1 + (taxaDeJurosPorPeriodo.doubleValue() / 100), periodos);

		BigDecimal resultado = null;
		if (somaAoValorOriginal) {
			resultado = valorOriginal.multiply(new BigDecimal(juros));
		} else {
			resultado = valorOriginal.multiply(new BigDecimal(juros)).subtract(valorOriginal);
		}

		return CurrencyUtil.arrendondaDuasCasasDecimais(resultado);
	}

	/**
	 * Arredonda um valor para duas casas decimais utilizando estratégia RoundingMode.HALF_EVEN:<br>
	 *
	 * Rounding mode to round towards the "nearest neighbor" unless both neighbors are equidistant, in which case, round towards the even neighbor. Behaves as for
	 * RoundingMode.HALF_UP if the digit to the left of the discarded fraction is odd; behaves as for RoundingMode.HALF_DOWN if it's even. Note that this is the rounding mode that
	 * statistically minimizes cumulative error when applied repeatedly over a sequence of calculations. It is sometimes known as "Banker's rounding," and is chiefly used in the
	 * USA. This rounding mode is analogous to the rounding policy used for float and double arithmetic in Java.
	 *
	 * Observe que este é o modo de arredondamento que minimiza estatisticamente o erro cumulativo quando aplicado repetidamente em uma seqüência de cálculos. É às vezes conhecido
	 * como "arredondamento do banqueiro", e é usado principalmente nos EUA.
	 *
	 * @param valor
	 * @return
	 */
	public static BigDecimal arrendondaDuasCasasDecimais(final BigDecimal valor) {
		return valor.divide(BigDecimal.ONE, 2, RoundingMode.HALF_EVEN);
	}

	public static BigDecimal calculaJurosSimples(final BigDecimal valorOriginal, final BigDecimal taxaDeJurosAM, Date dataCreditoTributario, Date dataVencimento) {

		if (DateUtil.isDateEquals(dataVencimento, dataCreditoTributario) || dataVencimento.before(dataCreditoTributario)) {
			return BigDecimal.ZERO;
		}

		dataCreditoTributario = DateUtil.addFirstDayOfMonth(DateUtil.getYearDate(dataCreditoTributario), DateUtil.getMonthDate(dataCreditoTributario));
		dataVencimento = DateUtil.addLastDayOfMonth(DateUtil.getYearDate(dataVencimento), DateUtil.getMonthDate(dataVencimento));

		final long meses = DateUtil.diferencaEmMeses(dataCreditoTributario, dataVencimento);

		final BigDecimal taxaDeJuros = taxaDeJurosAM.multiply(new BigDecimal(meses + 1));

		final BigDecimal valorJuros = valorOriginal.multiply(taxaDeJuros.divide(new BigDecimal(100)));

		return valorJuros;
	}

	public static BigDecimal calculaMulta(final BigDecimal valorOriginal, final BigDecimal multa, final Date dataCreditoTributario, final Date dataVencimento) {

		if (DateUtil.isDateEquals(dataVencimento, dataCreditoTributario) || dataVencimento.before(dataCreditoTributario)) {
			return BigDecimal.ZERO;
		}

		final BigDecimal valorMulta = valorOriginal.multiply(multa.divide(new BigDecimal(100)));

		return valorMulta;

	}

	public static BigDecimal calculaJurosIpca(final BigDecimal valorIpca, final BigDecimal valor) {

		final BigDecimal valorCorrigido = valor.multiply(valorIpca.divide(new BigDecimal(100)));

		return valorCorrigido;
	}

	public static BigDecimal calculaCorrecaoMonetariaIPCA(final BigDecimal valorIpca, final BigDecimal valorOriginal) {

		return valorOriginal.multiply(valorIpca.divide(new BigDecimal(100)));

	}

}
