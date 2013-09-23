package com.outgrader.proxy.advertisment.processor.internal.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public class TagUtils {

	public static Map<String, String> getAttributes(final String tagText) {
		StringTokenizer tokenizer = new StringTokenizer(tagText, " ");

		Map<String, String> result = new HashMap<>();

		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();

			int index = token.indexOf("=");

			if (index > 0) {
				String key = token.substring(0, index);
				String value = token.substring(index + 1);

				if (!StringUtils.isEmpty(value)) {
					value = value.substring(1, value.length() - 1);
				}

				result.put(key, value);
			}
		}

		return result;
	}
}
