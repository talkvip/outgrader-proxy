package com.outgrader.proxy.core.advertisment.storage.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.advertisment.rule.IAdvertismentRule;
import com.outgrader.proxy.advertisment.storage.IAdvertismentRuleStorage;
import com.outgrader.proxy.core.advertisment.rule.impl.BasicRule;
import com.outgrader.proxy.core.properties.IOutgraderProperties;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Singleton
public class AdvertismentRuleStorageImpl implements IAdvertismentRuleStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvertismentRuleStorageImpl.class);

	private enum LineType {
		COMMENT("!"), BASIC(null), ELEMENT_HIDING("#"), EXCLUDING("@@");

		private String symbol;

		private LineType(final String symbol) {
			this.symbol = symbol;
		}

		public static LineType getLineType(final String line) {
			for (LineType type : values()) {
				if ((type.symbol != null) && line.contains(type.symbol)) {
					return type;
				}
			}

			return LineType.BASIC;
		}
	}

	private final IOutgraderProperties properties;

	private final Collection<IAdvertismentRule> ruleSet;

	@Inject
	public AdvertismentRuleStorageImpl(final IOutgraderProperties properties) {
		this.properties = properties;

		ruleSet = initializeRuleSet();
	}

	@Override
	public Collection<IAdvertismentRule> getRules() {
		return ruleSet;
	}

	protected Collection<IAdvertismentRule> initializeRuleSet() {
		Collection<IAdvertismentRule> result = new ArrayList<>();

		try (InputStream stream = AdvertismentRuleStorageImpl.class.getResourceAsStream(properties.getAdvertismentListLocation())) {
			LineIterator lineIterator = IOUtils.lineIterator(stream, Charsets.UTF_8);

			while (lineIterator.hasNext()) {
				String line = lineIterator.next();

				LineType type = LineType.getLineType(line);

				if (type != null) {
					switch (type) {
					case BASIC:
						result.add(new BasicRule(line, Pattern.compile(formatRegexpLine(line))));
						break;
					default:
						// skip
						break;
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("An error occured during reading Advertisment list file", e);
		}

		return result;
	}

	private String formatRegexpLine(final String line) {
		String result = line;

		result = result.replaceAll(Pattern.quote("+"), Pattern.quote("+"));
		result = result.replaceAll(Pattern.quote("&"), Pattern.quote("&"));
		result = result.replaceAll(Pattern.quote("/"), Pattern.quote("/"));
		result = result.replaceAll(Pattern.quote("*"), Pattern.quote("*"));
		result = result.replaceAll(Pattern.quote("?"), Pattern.quote("?"));
		result = result.replaceAll(Pattern.quote("["), Pattern.quote("["));
		result = result.replaceAll(Pattern.quote("]"), Pattern.quote("]"));

		return result;
	}
}
