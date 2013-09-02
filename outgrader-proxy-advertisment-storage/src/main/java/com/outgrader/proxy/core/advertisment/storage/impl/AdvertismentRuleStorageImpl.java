package com.outgrader.proxy.core.advertisment.storage.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.outgrader.proxy.core.advertisment.rule.impl.BasicRule;
import com.outgrader.proxy.core.advertisment.rule.impl.BasicRule.BasicRuleBuilder;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Component
public class AdvertismentRuleStorageImpl implements IAdvertismentRuleStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvertismentRuleStorageImpl.class);

	private enum LineType {
		COMMENT("!", true), BASIC(null), ELEMENT_HIDING("#"), EXCLUDING("@@");

		private String symbol;

		private boolean shouldStart;

		private LineType(final String symbol) {
			this(symbol, false);
		}

		private LineType(final String symbol, final boolean shouldStart) {
			this.symbol = symbol;
			this.shouldStart = shouldStart;
		}

		public static LineType getLineType(final String line) {
			for (LineType type : values()) {
				if ((type.symbol != null) && (type.shouldStart ? line.startsWith(type.symbol) : line.contains(type.symbol))) {
					return type;
				}
			}

			return LineType.BASIC;
		}
	}

	private final IOutgraderProperties properties;

	private final ThreadLocal<IAdvertismentRule[]> ruleSet = new ThreadLocal<IAdvertismentRule[]>() {
		@Override
		protected IAdvertismentRule[] initialValue() {
			return initializeRuleSet();
		}
	};

	@Inject
	public AdvertismentRuleStorageImpl(final IOutgraderProperties properties) throws Exception {
		this.properties = properties;
	}

	@Override
	public IAdvertismentRule[] getRules() {
		return ruleSet.get();
	}

	protected IAdvertismentRule[] initializeRuleSet() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("started initializeRuleSet()");
		}

		LOGGER.info("Initializing rule set from <" + properties.getAdvertismentListLocation() + ">");

		Collection<IAdvertismentRule> result = new ArrayList<>();

		try (InputStream stream = AdvertismentRuleStorageImpl.class.getResourceAsStream(properties.getAdvertismentListLocation())) {
			LineIterator lineIterator = IOUtils.lineIterator(stream, Charsets.UTF_8);

			while (lineIterator.hasNext()) {
				String line = lineIterator.next();

				LineType type = LineType.getLineType(line);

				if (type != null) {
					try {
						switch (type) {
						case BASIC:
							result.add(getBasicRule(line));
							break;
						default:
							// skip
							break;
						}
					} catch (Exception e) {
						LOGGER.error("An error occured during processing rule <" + line + "> by type <" + type + ">", e);

						throw e;
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("An error occured during reading Advertisment list file", e);
		}

		LOGGER.info("It was loaded <" + result.size() + "> rules");

		return result.toArray(new IAdvertismentRule[result.size()]);
	}

	private BasicRule getBasicRule(final String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, "*", false);
		BasicRuleBuilder builder = new BasicRuleBuilder(line);

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			// int endsWithIndex = token.indexOf("!");
			// if (endsWithIndex != StringUtils.INDEX_NOT_FOUND) {
			// builder.shouldEndWith(token.substring(0, endsWithIndex));
			// } else {
			// int startsWithIndex = token.indexOf("||");
			//
			// if (startsWithIndex != StringUtils.INDEX_NOT_FOUND) {
			// builder.shouldStartWith(token.substring(startsWithIndex + 2));
			// } else {
			builder.shouldContain(token);
			// }
			// }
		}

		return builder.build();
	}
}
