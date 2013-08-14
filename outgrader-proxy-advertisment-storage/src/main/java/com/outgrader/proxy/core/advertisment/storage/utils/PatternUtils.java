package com.outgrader.proxy.core.advertisment.storage.utils;

import java.util.regex.Pattern;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public final class PatternUtils {

	private PatternUtils() {

	}

	private static final String[] ESCAPING_SYMBOLS = { "+", "[", "]", "?", "|" };

	public static Pattern createPattern(final String ruleLine) {
		String escaped = escapeSymbols(ruleLine);

		String extended = extendPattern(escaped);

		String asterisked = processAsterisk(extended);

		return Pattern.compile(asterisked);
	}

	private static final String processAsterisk(final String original) {
		String subResult = original.replace("*", ".*");

		return subResult.replace(".*.*", ".*");
	}

	private static final String extendPattern(final String pattern) {
		return "*" + pattern + "*";
	}

	private static final String escapeSymbols(final String ruleLine) {
		String result = ruleLine;
		for (String symbol : ESCAPING_SYMBOLS) {
			result = result.replace(symbol, Pattern.quote(symbol));
		}

		return result;
	}
}
