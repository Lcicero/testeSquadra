package com.br.testesquadra.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.br.testesquadra.util.exception.BusinessException;
import com.br.testesquadra.util.exception.SystemException;



/**
 * Utility class to handle Dates
 */
public class DateUtil extends DateUtils {

	private static int FIRST_DAY_OF_MONTH = 1;

	/** Default constructor */
	protected DateUtil() {
		// Utility class
	}

	/**
	 * Get a new instance of the default calendar implementation
	 *
	 * @param date
	 *            Date to initialize the calendar (Optional)
	 *
	 * @return <code>Calendar</code>
	 */
	public static Calendar getCurrentCalendar(final Date date) {

		final Calendar calendar = new GregorianCalendar();

		if (date != null) {
			calendar.setTime(date);
		}

		return calendar;
	}

	public static int getFirstDayOfMonth() {

		return DateUtil.FIRST_DAY_OF_MONTH;
	}

	/**
	 * Get a new instance of the default calendar implementation
	 *
	 * @return <code>Calendar</code>
	 */
	public static Calendar getCurrentCalendar() {

		return DateUtil.getCurrentCalendar(new Date());
	}

	/**
	 * Checks if the date is an weekend
	 *
	 * @param date
	 *            Date to be tested
	 *
	 * @return True if date is an weekend
	 */
	public static boolean isWeekend(final Date date) {

		final Calendar calendar = DateUtil.getCurrentCalendar(date);
		final int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);

		return (diaSemana == Calendar.SATURDAY) || (diaSemana == Calendar.SUNDAY);

	}

	/**
	 * Checks if the date is Saturday.
	 *
	 * @param date
	 *            Date to be verified
	 *
	 * @return <code>Boolean</code>
	 */
	public static boolean isSaturday(final Date date) {

		return DateUtil.getCurrentCalendar(date).get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}

	/**
	 * Checks if the date is Sunday.
	 *
	 * @param date
	 *            Date to be verified
	 *
	 * @return <code>Boolean</code>
	 */
	public static boolean isSunday(final Date date) {

		return DateUtil.getCurrentCalendar(date).get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	/**
	 * Utility method to format a Date into a date/time string.
	 *
	 * @param date
	 *            the time value to be formatted into a time string.
	 * @param pattern
	 *            Using the given pattern to the default date format.
	 *
	 * @return the formatted time string.
	 */
	public static String formatDate(final Date date, final String pattern) {

		if (date == null) {
			return null;
		}

		final SimpleDateFormat formatter = new SimpleDateFormat(pattern, new Locale("pt", "BR"));
		return formatter.format(date);
	}

	/**
	 * @param date
	 * @return data formatada no padrão dd/MM/yyyy HH:mm:ss
	 */
	public static String formatDateTime(final Date date) {

		if (date == null) {
			return null;
		}

		return DateUtil.formatDate(date, "dd/MM/yyyy HH:mm:ss");
	}

	/**
	 * @param date
	 * @return data formatada no padrão dd/MM/yyyy
	 */
	public static String formatDate(final Date date) {

		if (date == null) {
			return null;
		}

		return DateUtil.formatDate(date, "dd/MM/yyyy");
	}

	/**
	 * Utility method to convert a <code>String</code> into <code>Date</code>
	 *
	 * @param date
	 *            A <code>String</code> whose beginning should be parsed.
	 * @param pattern
	 *            Using the given pattern to the default date format.
	 *
	 * @return <code>XMLGregorianCalendar</code>
	 */
	public static Date parseDate(final String date, final String pattern) {

		final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		try {
			return formatter.parse(date);
		} catch (final ParseException e) {
			throw new SystemException("Error parsing date", e);
		}
	}

	/**
	 * Utility method to convert a date into a <code>XMLGregorianCalendar</code>
	 *
	 * @param date
	 *            A <code>Date</code> whose beginning should be parsed.
	 *
	 * @return <code>XMLGregorianCalendar</code>
	 */
	public static XMLGregorianCalendar parseDateToXMLGregorianCalendar(final Date date) {

		try {

			final GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);

			return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (final DatatypeConfigurationException dce) {
			throw new SystemException("Error converting GregorianCalendar into XMLGregorainCalendar", dce);
		}
	}

	/**
	 * Utility method to converter string in <code>XMLGregorianCalendar</code>
	 *
	 * @param string
	 *            A <code>Date</code> whose beginning should be parsed.
	 * @param pattern
	 *            Using the given pattern to the default date format.
	 *
	 * @return <code>XMLGregorianCalendar</code>
	 */
	public static XMLGregorianCalendar parseStringToXMLGregorianCalendar(final String date, final String pattern) {

		try {

			final GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(DateUtil.parseDate(date, pattern));

			return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (final DatatypeConfigurationException dce) {
			throw new BusinessException("Erro ao converter GregorianCalendar para XMLGregorianCalendar", dce);
		}
	}

	/**
	 * Utility method to convert a <code>XMLGregorianCalendar</code> into a <code>Date</code>
	 *
	 * @param xmlDate
	 *            A <code>XMLGregorianCalendar</code> whose beginning should be parsed.
	 *
	 * @return <code>Date</code>
	 */
	public static Date parseXMLGregorianCalendarToDate(final XMLGregorianCalendar xmlDate) {

		return xmlDate != null ? xmlDate.toGregorianCalendar().getTime() : null;
	}

	/**
	 * Utility method to convert a <code>XMLGregorianCalendar</code> into a <code>String</code>
	 *
	 * @param xmlDate
	 *            <code>XMLGregorianCalendar</code> to be converted
	 * @param pattern
	 *            Pattern to be used to convert
	 *
	 * @return <code>Date</code>
	 */
	public static String formatXMLGregorianCalendarToString(final XMLGregorianCalendar xmlDate, final String pattern) {

		return xmlDate != null ? DateUtil.formatDate(xmlDate.toGregorianCalendar().getTime(), pattern) : null;
	}

	public static boolean isSameYear(final Date date, final int year) {

		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final int yearFromDate = cal.get(Calendar.YEAR);

		return yearFromDate == year;
	}

	public static Date createDateFirtsDayMonth(final int year, final int month) {

		return DateUtil.createDate(year, month, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);
	}

	public static Date createDateLastDayMonth(final int year, final int month) {

		return DateUtil.createDate(year, month, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
	}

	public static Date createDate(final int year, final int month, final int day, final int hour, final int minute, final int second) {

		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * Utility method to convert a <code>String</code> into <code>Date</code>
	 *
	 * @param date
	 *            A <code>String</code> whose beginning should be parsed.
	 * @param pattern
	 *            Using the given pattern to the default date format.
	 *
	 * @return <code>XMLGregorianCalendar</code>
	 * @throws ParseException
	 *             Parse Error
	 */
	public static Date parseDateWhitException(final String date, final String pattern) throws ParseException {

		final SimpleDateFormat formatter = new SimpleDateFormat(pattern);

		return formatter.parse(date);

	}

	public static Date addFirstHourDay(final Date data) {

		if (data == null) {
			return null;
		}

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);

		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();

	}

	public static Date addLastHourDay(final Date data) {

		if (data == null) {
			return null;
		}

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);

		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);

		return calendar.getTime();

	}

	public static long diferencaEmHorasEntreDatas(final Date dataInicial, final Date dataFinal) {

		final Calendar dataInicialCalendar = Calendar.getInstance();
		dataInicialCalendar.setTime(dataInicial);

		final Calendar dataFinalCalendar = Calendar.getInstance();
		dataFinalCalendar.setTime(dataFinal);

		final long diferenca = dataFinalCalendar.getTimeInMillis() - dataInicialCalendar.getTimeInMillis();

		final long diferencaHoras = diferenca / (60 * 60 * 1000);

		return diferencaHoras;

	}

	public static Integer getDayDate(final Date date) {

		if (date == null) {
			return null;
		}
		final LocalDate localDate = DateUtil.asLocalDate(date);
		return localDate.getDayOfMonth();
	}

	public static Integer getMonthDate(final Date date) {

		if (date == null) {
			return null;
		}
		final LocalDate localDate = DateUtil.asLocalDate(date);
		return localDate.getMonthValue();
	}

	public static Integer getYearDate(final Date date) {

		if (date == null) {
			return null;
		}
		final LocalDate localDate = DateUtil.asLocalDate(date);
		return localDate.getYear();
	}

	public static Date asDate(final LocalDate localDate) {

		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(final LocalDateTime localDateTime) {

		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(final Date date) {

		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(final Date date) {

		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static long diferencaEmDias(final LocalDate dataInicio, final LocalDate dataFim) {

		return ChronoUnit.DAYS.between(dataInicio, dataFim);
	}

	public static long diferencaEmMeses(final LocalDate dataInicio, final LocalDate dataFim) {

		return ChronoUnit.MONTHS.between(dataInicio, dataFim);
	}

	public static long diferencaEmAnos(final LocalDate dataInicio, final LocalDate dataFim) {

		return ChronoUnit.YEARS.between(dataInicio, dataFim);
	}

	public static long diferencaEmDias(final Date dataInicio, final Date dataFim) {

		return ChronoUnit.DAYS.between(DateUtil.asLocalDate(dataInicio), DateUtil.asLocalDate(dataFim));
	}

	public static long diferencaEmMeses(final Date dataInicio, final Date dataFim) {

		return ChronoUnit.MONTHS.between(DateUtil.asLocalDate(dataInicio), DateUtil.asLocalDate(dataFim));
	}

	public static long diferencaEmAnos(final Date dataInicio, final Date dataFim) {

		return ChronoUnit.YEARS.between(DateUtil.asLocalDate(dataInicio), DateUtil.asLocalDate(dataFim));
	}

	/**
	 * Converte Uma Date do java.util.Date para um java.util.LocalDate
	 *
	 * @param date
	 * @return a data convertida em LocalDate
	 */
	public static LocalDate dateToDateTime(final Date date) {

		if (date == null) {
			return null;
		}

		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Retorna a idade passado uma data
	 *
	 * @param data
	 * @return
	 */
	public static Integer getIdade(final LocalDate dataNascimento) {

		final Period period = Period.between(dataNascimento, LocalDate.now());

		return period.getYears();
	}

	/**
	 * Obter Ano Atual
	 *
	 * @param
	 * @return
	 */
	public static Integer getAnoAtual() {

		return LocalDate.now().getYear();

	}

	/**
	 * Obter o mês atual.
	 *
	 * @param
	 * @return
	 */
	public static Integer getMesAtual() {

		return LocalDate.now().getMonthValue();

	}

	/**
	 *
	 * @param dataInicial
	 * @param dataFinal
	 * @return retorna true caso a data fim for maior que a data de inicio passada
	 */
	public static boolean isDataFimMaiorDataInicio(final LocalDateTime dataInicial, final LocalDateTime dataFinal) {

		return dataFinal.isAfter(dataInicial);
	}

	/**
	 * @param dataInicial
	 * @param dataFinal
	 * @return retorna true caso a data de inicio seja maior que a data fim passada
	 */
	public static boolean isDataInicioMaiorDataFim(final LocalDateTime dataInicial, final LocalDateTime dataFinal) {

		return dataInicial.isAfter(dataFinal);
	}

	/**
	 * @param data
	 * @return retorna true caso a data passada seja maior que a data atual
	 */
	public static boolean isDataMaiorDataAtual(final LocalDateTime data) {

		return data.isBefore(LocalDateTime.now());

	}

	/**
	 * @param dataAtual
	 * @param dataInicio
	 * @param dataFim
	 * @return retorna true caso a data atual esteja entre a data de inicio e a data fim passadas
	 *
	 */
	public static boolean validarDataAtualEntreDataIncioDataFim(final LocalDateTime dataAtual, final LocalDateTime dataInicio, final LocalDateTime dataFim) {

		return dataInicio.isBefore(dataAtual) && dataFim.isAfter(dataAtual);
	}

	/**
	 * @param date
	 * @param dias
	 * @return retorna uma nova data somando os dias passados
	 */
	public static LocalDateTime addDias(final LocalDateTime date, final int dias) {

		return date.plusDays(dias);

	}

	/**
	 * @param date
	 * @param mes
	 * @return retorna uma nova data adicionando os meses passados
	 */
	public static LocalDateTime addMes(final LocalDateTime date, final int mes) {

		return date.plusMonths(mes);

	}

	/**
	 * @param date
	 * @param horas
	 * @return retorna uma nova data adicionando as horas passadas
	 */
	public static LocalDateTime addHoras(final LocalDateTime date, final int horas) {

		return date.plusHours(horas);

	}

	/**
	 * @param date
	 * @param minutos
	 * @return retorna uma nova data adicionando os minutos passados por parâmetro
	 */
	public static LocalDateTime addMinutos(final LocalDateTime date, final int minutos) {

		return date.plusMinutes(minutos);

	}

	/**
	 * @return retorna a data atual com as horas zeradas
	 */
	public static LocalDateTime getDiaAtualHoraZero() {

		return DateUtil.zerarHoras(LocalDateTime.now());

	}

	/**
	 * @param data
	 * @return retorna a data passada com as hora, minuto, segundo e milessegundos zerados
	 */
	public static LocalDateTime zerarHoras(final LocalDateTime data) {

		return LocalDateTime.of(data.getYear(), data.getMonth().getValue(), data.getDayOfMonth(), 0, 0, 0, 0);

	}

	/**
	 * @param data
	 * @return adiciona a data passada a hora 23, minuto 59, segundo 59, milessegundo 999999999
	 */
	public static LocalDateTime completarUltimaHoraDoDia(final LocalDateTime data) {

		return LocalDateTime.of(data.getYear(), data.getMonth().getValue(), data.getDayOfMonth(), 23, 59, 59, 999999999);

	}

	/**
	 * @param dataInicial
	 * @param dataFinal
	 * @param diasPermitidos
	 * @return retorna true caso a diferença em dias entra a data inicial e a data final seja maior que o numero passado no parametro diasPermitidos
	 */
	public static boolean isDataMaiorQuePeriodoPermitido(final LocalDate dataInicial, final LocalDate dataFinal, final int diasPermitidos) {

		return DateUtil.diferencaEmDias(dataInicial, dataFinal) > diasPermitidos;

	}

	/**
	 * @param data
	 * @param dias
	 * @return retorna uma nova data com os a quantidade de dias subtraida
	 */
	public static LocalDateTime subtrairDia(final LocalDateTime data, final int dias) {

		return data.minusDays(dias);

	}

	/**
	 * Retorna o mês de data repassada.
	 *
	 * @param data
	 * @return
	 */
	public static Integer getMesData(final LocalDate data) {

		return data.getMonthValue();

	}

	/**
	 * Retorna o ano de data repassada.
	 *
	 * @param data
	 * @return
	 */
	public static Integer getAnoData(final LocalDate data) {

		return data.getYear();
	}

	/**
	 * @param ano
	 * @param mes
	 * @param dia
	 * @return retorna uma nova data montada atraves dos parametros informados
	 */
	public static LocalDateTime montaData(final int ano, final int mes, final int dia) {

		return LocalDateTime.of(ano, mes, dia, 0, 0, 0, 0);

	}

	public static String getMesFormatado(final Integer mesAtual) {

		if (mesAtual == null) {
			return "";
		}

		final Month month = Month.of(mesAtual);

		return month.getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
	}

	public static int getLastDayOfMonth(final Integer year, final Integer month) {

		final LocalDate localDate = LocalDate.of(year, month, DateUtil.FIRST_DAY_OF_MONTH);

		return localDate.lengthOfMonth();
	}

	public static Date addLastDayOfMonth(final Integer year, final Integer month) {

		return DateUtil.asDate(DateUtil.montaData(year, month, DateUtil.getLastDayOfMonth(year, month)));
	}

	public static Date addFirstDayOfMonth(final Integer year, final Integer month) {

		return DateUtil.asDate(DateUtil.montaData(year, month, DateUtil.getFirstDayOfMonth()));
	}

	public static Date subtrairMes(final int quantidade, final Date date) {

		LocalDate localDate = DateUtil.asLocalDate(date);

		localDate = localDate.minusMonths(quantidade);

		return DateUtil.asDate(localDate);
	}

	public static Date getProximoDiaUtilOuAtual(LocalDateTime data) {

		while ((data.getDayOfWeek().getValue() == 6) || (data.getDayOfWeek().getValue() == 7)) {

			data = data.plusDays(1l);

		}

		return DateUtil.asDate(data);
	}

	public static Date adicionarDataUltimoDiaAno(final Date data) {

		final long ano = DateUtil.getAnoData(DateUtil.asLocalDate(data));

		return DateUtil.asDate(DateUtil.montaData((int) ano, 12, 31));

	}

	public static Date adicionarPrimeiroDiaAno(final Date data) {

		final long ano = DateUtil.getAnoData(DateUtil.asLocalDate(data));

		return DateUtil.asDate(DateUtil.montaData((int) ano, 1, 1));

	}

	/**
	 * Verifica se data á sábado ou domingo e acrescenta dias conforme necessário p/ retornar dia de semana.
	 *
	 * @param Date
	 *            *
	 *
	 * @return Date
	 */
	public static Date notWeekendDate(final Date adjustableDate) {

		final Calendar data = Calendar.getInstance();

		data.setTime(adjustableDate);

		// se for domingo
		if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			data.add(Calendar.DATE, 1);
		}
		// se for sábado
		else if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			data.add(Calendar.DATE, 2);
		}

		final Date newDate = data.getTime();

		return newDate;
	}

	/**
	 * Returna data de 5 anos a traz
	 *
	 * @param Date
	 */
	public static Date dateFromFiveYearAgo() {

		final Calendar data = Calendar.getInstance();

		data.add(Calendar.YEAR, -5);

		final Date newDate = data.getTime();

		return newDate;
	}

	public static Boolean isDateBetween(final Date dataBase, final Date dataInicio, final Date dataFim) {

		return (dataBase.compareTo(dataInicio) >= 0) && (dataBase.compareTo(dataFim) <= 0);
	}

	public static boolean isDateEquals(final Date data1, final Date data2) {

		if ((data1 == null) || (data2 == null)) {
			return false;
		}

		final LocalDateTime date1 = DateUtil.zerarHoras(DateUtil.asLocalDateTime(data1));
		final LocalDateTime date2 = DateUtil.zerarHoras(DateUtil.asLocalDateTime(data2));

		return date1.equals(date2);
	}

	public static boolean isDateTimeEquals(final Date data1, final Date data2) {

		return data1.equals(data2);
	}

	/**
	 * Retorna a data no tipo java.util.Date
	 *
	 * @param Date
	 *            no formato yyyyMMdd
	 */
	public static Date stringToDate(final String data) {

		if (StringUtils.isEmpty(data)) {
			return null;
		}

		final DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		try {
			return formatter.parse(data);
		} catch (final ParseException e) {
			throw new BusinessException(e);
		}
	}

	public static boolean validaDataValidaFormatoYYYYMM(final String data) {

		if (data == null) {
			return false;
		}

		if (data.length() != 6) {
			return false;
		}

		try {
			Integer.parseInt(data);
		} catch (final NumberFormatException e) {
			return false;
		}

		final Integer mes = Integer.parseInt(data.substring(4, 6));

		if ((mes > 12) || (mes < 1)) {
			return false;
		}

		return true;

	}

	public static String getMesAnoFormatadoMMYYYY(final Integer competencia) {

		return DateUtil.getMesDataFormatoYYYYMM(competencia) + "/" + DateUtil.getAnoDataFormatoYYYYMM(competencia);
	}

	public static Integer getMesDataFormatoYYYYMM(final Integer competencia) {

		if (competencia == null) {
			return null;
		}

		try {

			return Integer.parseInt(competencia.toString().substring(4, 6));

		} catch (final Exception e) {
			throw new BusinessException(e);
		}
	}

	public static Integer getAnoDataFormatoYYYYMM(final Integer competencia) {

		if (competencia == null) {
			return null;
		}

		try {

			return Integer.parseInt(competencia.toString().substring(0, 4));

		} catch (final Exception e) {
			throw new BusinessException(e);
		}
	}

	public static boolean isDateEquals(final XMLGregorianCalendar competencia, final Date dataCompetencia) {

		final XMLGregorianCalendar parseDateToXMLGregorianCalendar = DateUtil.parseDateToXMLGregorianCalendar(dataCompetencia);

		return competencia.compare(parseDateToXMLGregorianCalendar) == 0;
	}

	public XMLGregorianCalendar dateToXmlGregorianCalendar(final Date date) {

		if (date == null) {
			return null;
		}

		try {
			final GregorianCalendar c = new GregorianCalendar();
			c.setTime(date);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (final DatatypeConfigurationException ex) {
			throw new RuntimeException(ex);
		}
	}
}
