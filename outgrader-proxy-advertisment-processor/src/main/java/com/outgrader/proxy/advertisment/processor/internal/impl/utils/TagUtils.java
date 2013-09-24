package com.outgrader.proxy.advertisment.processor.internal.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public class TagUtils {

	private static final Pattern ATTR_TAG_PATTERN = Pattern.compile("(\\S+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?");

	public static Map<String, String> getAttributes(final String tagText) {
		Map<String, String> result = new HashMap<>();

		Matcher matcher = ATTR_TAG_PATTERN.matcher(tagText);

		while (matcher.find()) {
			result.put(matcher.group(1), matcher.group(2));
		}

		return result;
	}
}
