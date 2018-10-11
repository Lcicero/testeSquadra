package com.br.testesquadra.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class FileUtilsHelper {

	/**
	 * @param data
	 * @param fileName
	 * @return Full file name
	 * @throws IOException
	 */
	public static String generateTmpFile(final String data, final String fileName) throws IOException {
		final String fullFileName = FilenameUtils.separatorsToSystem(FilenameUtils.concat(System.getProperty("java.io.tmpdir"), fileName));
		final File file = new File(fullFileName);
		FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8.toString());
		return fullFileName;
	}

	/**
	 *
	 * @param filePath
	 * @return File content
	 * @throws IOException
	 */
	public static String readFileAsString(final String filePath) throws IOException {
		return FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8.toString());
	}
}
