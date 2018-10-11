package com.br.testesquadra.util;

import java.util.HashSet;
import java.util.Set;

import com.br.testesquadra.util.exception.NotADigitException;


/**
 * Utility methods to validate several document numbers that use MOD11 verification digits.
 */
public final class Mod11 {
	private static final Set<String> INVALID_CPF = new HashSet<>();
	private static final Set<String> INVALID_CNPJ = new HashSet<>(1);

	static {
		INVALID_CPF.add("00000000000");
		INVALID_CPF.add("11111111111");
		INVALID_CPF.add("22222222222");
		INVALID_CPF.add("33333333333");
		INVALID_CPF.add("44444444444");
		INVALID_CPF.add("55555555555");
		INVALID_CPF.add("66666666666");
		INVALID_CPF.add("77777777777");
		INVALID_CPF.add("88888888888");
		INVALID_CPF.add("99999999999");

		INVALID_CNPJ.add("00000000000000");

	}

	/**
	 * Default constructor.
	 */
	protected Mod11() {
	}

	/**
	 * Validates a brazilian CPF number.
	 * 
	 * @param input
	 *            CPF number (only numbers, no mask).
	 * 
	 * @return <code>true</code> if the given number is valid.
	 */
	public static final boolean validateCPF(final String number) {
		if (number == null || number.length() != 11) {
			return false;
		}

		return !INVALID_CPF.contains(number) && validateMod11(number, 0, 8, 9, false, 1, 9) && validateMod11(number, 0, 9, 10, false, 0, 9);
	}

	/**
	 * Validates a brazilian CNPJ number.
	 * 
	 * @param input
	 *            CNPJ number (only numbers, no mask).
	 * 
	 * @return <code>true</code> if the given number is valid.
	 */
	public static final boolean validateCNPJ(final String number) {
		if (number == null || number.length() != 14) {
			return false;
		}

		return !INVALID_CNPJ.contains(number) && validateMod11(number, 11, 0, 12, false, 9, 2) && validateMod11(number, 12, 0, 13, false, 9, 2);
	}

	/**
	 * Calculates the MOD11 digit for the given input up to the verifier digit and checks if it matches the verifier digit.
	 * 
	 * @param input
	 *            Input to validate.
	 * @param verifierIndex
	 *            Index of the verifier digit. Negative values are taken to be relative to the last character.
	 * 
	 * @return <code>true</code> if the given input is valid.
	 */
	public static final boolean validateMod11(final String input, final int verifierIndex) {
		return validateMod11(input, 0, verifierIndex - 1, verifierIndex, false, 0, verifierIndex - 1);
	}

	/**
	 * Calculates the MOD11 digit for the given input up to the verifier digit and checks if it matches the verifier digit.
	 * 
	 * @param input
	 *            Input to validate.
	 * @param verifierIndex
	 *            Index of the verifier digit. Negative values are taken to be relative to the last character.
	 * @param x
	 *            If true, use "X" instead of "0" for cases where the final remainder is 10.
	 * 
	 * @return <code>true</code> if the given input is valid.
	 */
	public static final boolean validateMod11(final String input, final int verifierIndex, final boolean x) {
		return validateMod11(input, 0, verifierIndex - 1, verifierIndex, x, 0, verifierIndex - 1);
	}

	/**
	 * Calculates the MOD11 digit for the given input subsequence and checks if it matches the verifier digit.
	 * 
	 * @param input
	 *            Input to validate.
	 * @param begin
	 *            Subsequence start.
	 * @param end
	 *            Subsequence end.
	 * @param verifierIndex
	 *            Index of the verifier digit. Negative values are taken to be relative to the last character.
	 * 
	 * @return <code>true</code> if the given input is valid.
	 */
	public static final boolean validateMod11(final String input, final int begin, final int end, final int verifierIndex) {
		return validateMod11(input, begin, end, verifierIndex, false, begin, end);
	}

	/**
	 * Calculates the MOD11 digit for the given input subsequence and checks if it matches the verifier digit.
	 * 
	 * @param input
	 *            Input to validate.
	 * @param begin
	 *            Subsequence start.
	 * @param end
	 *            Subsequence end.
	 * @param verifierIndex
	 *            Index of the verifier digit. Negative values are taken to be relative to the last character.
	 * @param x
	 *            If true, use "X" instead of "0" for cases where the final remainder is 10.
	 * @param firstNumber
	 *            the first number in the range used to multiply the digits.
	 * @param lastNumber
	 *            the last number in the range used to multiply the digits.
	 * 
	 * @return <code>true</code> if the given input is valid.
	 */
	public static final boolean validateMod11(final String input, final int begin, final int end, int verifierIndex, final boolean x, final int firstNumber, final int lastNumber) {
		if (verifierIndex < 0) {
			verifierIndex += input.length();
		}

		try {
			return mod11(input, begin, end, x, firstNumber, lastNumber) == input.charAt(verifierIndex);
		} catch (final NotADigitException e) {
			return false;
		}
	}

	/**
	 * Calculates the MOD11 digit for the given input subsequence.
	 * 
	 * @param input
	 *            Input to validate.
	 * @param begin
	 *            Subsequence start.
	 * @param end
	 *            Subsequence end.
	 * 
	 * @return MOD11 verifier digit.
	 */
	public static final char mod11(final String input, final int begin, final int end) {
		return mod11(input, begin, end, false, begin, end);
	}

	/**
	 * Calculates the MOD11 digit for the given input subsequence.
	 * 
	 * @param input
	 *            Input to validate.
	 * @param begin
	 *            Subsequence start.
	 * @param end
	 *            Subsequence end.
	 * @param x
	 *            If true, use "X" instead of "0" for cases where the final remainder is 10.
	 * @param firstNumber
	 *            the first number in the range used to multiply the digits.
	 * @param lastNumber
	 *            the last number in the range used to multiply the digits.
	 * 
	 * @return MOD11 verifier digit.
	 * 
	 * @throws NotADigitException
	 *             If an expected numeric digit is not a decimal digit.
	 */
	public static final char mod11(final String input, final int begin, final int end, final boolean x, final int firstNumber, final int lastNumber) {

		int increment = 1;
		int incrementMultiply = 1;
		if (end < begin) {
			increment = -1;
		}
		if (lastNumber < firstNumber) {
			incrementMultiply = -1;
		}

		int soma = 0;
		int multiplyBy = firstNumber - incrementMultiply;
		int i = begin - increment;
		while (i != end) {
			i += increment;
			multiplyBy += incrementMultiply;
			final int digit = Character.digit(input.charAt(i), 10);

			if (digit < 0) {
				throw new NotADigitException(input.charAt(i));
			}

			soma += digit * multiplyBy;
			if (multiplyBy == lastNumber) {
				multiplyBy = firstNumber - incrementMultiply;
			}
		}

		final int resto = soma % 11;

		switch (resto) {
		case 11:
			return '0';

		case 10:
			return x ? 'X' : '0';

		default:
			return Character.forDigit(resto, 10);
		}
	}

}
