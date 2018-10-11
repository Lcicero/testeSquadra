package com.br.testesquadra.util;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * Classe Utilitária de Logger Proprietário
 *
 * @author leandro.ferreira
 *
 */
public class LoggerUtil {

	private static final String FILE_LOG_NAME = "log_personalizado.log";

	private static final BigInteger bytesInMegaBytes = BigInteger.valueOf(1048576);

	private static final BigInteger maxFileSizeInMegaBytes = BigInteger.valueOf(5);

	private final String className;

	private File file;

	public LoggerUtil(final Class<?> clazz) {
		super();
		this.className = clazz.getSimpleName();
		this.initLogFile();
	}

	private void initLogFile() {

		this.file = new File(this.getFilePath() + File.separator + LoggerUtil.FILE_LOG_NAME);
	}

	/**
	 * Retorna o path do domain home do weblogic
	 *
	 * @return
	 */
	private String getFilePath() {

		final String filePath = System.getProperty("user.dir");
		if ((filePath == null) || filePath.isEmpty()) {
			throw new RuntimeException("Não foi possível encontrar a pasta temporária para escrita do LOG personalizado!");
		}
		return filePath;
	}

	private void rotateLogFile() {

		if (!this.hasToRotate()) {
			return;
		}

		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			FileUtils.moveFile(this.file, new File(this.getFilePath() + File.separator + LoggerUtil.FILE_LOG_NAME + sdf.format(this.file.lastModified())));

			this.initLogFile();

		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	private boolean hasToRotate() {

		if (FileUtils.sizeOfAsBigInteger(this.file).compareTo(BigInteger.ZERO) <= 0) {
			return false;
		}

		if (FileUtils.isFileOlder(this.file, DateUtil.asDate(DateUtil.getDiaAtualHoraZero()))) {
			return true;
		}

		final BigInteger fileSizeInMB = FileUtils.sizeOfAsBigInteger(this.file).divide(LoggerUtil.bytesInMegaBytes);

		if (fileSizeInMB.compareTo(LoggerUtil.maxFileSizeInMegaBytes) == 1) {
			return true;
		}

		return false;
	}

	public void writeLog(final String logMsg) {

		this.rotateLogFile();

		final String log = DateUtil.formatDateTime(new Date()) + " - " + System.getProperty("weblogic.Name") + " - " + this.className + " - " + logMsg;
		try {
			FileUtils.writeStringToFile(this.file, log + System.lineSeparator(), Charset.defaultCharset(), true);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
