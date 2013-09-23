package com.outgrader.proxy.core.advertisment.storage.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.advertisment.filter.IFilterSource;
import com.outgrader.proxy.core.advertisment.filter.impl.FilterBuilderUtils;
import com.outgrader.proxy.core.advertisment.rule.impl.internal.CurrentTagRule;
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

	private static final String DOMAIN_PREFIX = "domain=";

	private static final String PARAMETERS_SEPARATOR = "$";

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvertismentRuleStorageImpl.class);

	private enum LineType {
		COMMENT("!", true), BASIC(null), ELEMENT_HIDING("#"), EXCLUDING("@@", true), EXTENDED(PARAMETERS_SEPARATOR);

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
				if ((type.symbol != null)) {
					if (type.shouldStart && line.startsWith(type.symbol)) {
						return type;
					} else if (line.contains(type.symbol)) {
						return type;
					}
				}
			}

			return LineType.BASIC;
		}
	}

	private final IOutgraderProperties properties;

	private IAdvertismentRule[] includingRuleSet;

	private IAdvertismentRule[] excludingRuleSet;

	@Inject
	public AdvertismentRuleStorageImpl(final IOutgraderProperties properties) throws Exception {
		this.properties = properties;
	}

	@Override
	public IAdvertismentRule[] getIncludingRules() {
		return includingRuleSet;
	}

	protected InputStream openRuleFileStream(final String location) {
		return AdvertismentRuleStorageImpl.class.getResourceAsStream(location);
	}

	@PostConstruct
	protected void initializeRuleSet() throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("started initializeRuleSet()");
		}

		Collection<IAdvertismentRule> mainRules = new ArrayList<>();
		Collection<IAdvertismentRule> excludingRules = new ArrayList<>();

		int ruleCount = 0;

		for (String location : properties.getAdvertismentListLocations()) {
			LOGGER.info("Initializing rule set from <" + location + ">");

			try (InputStream stream = openRuleFileStream(location)) {
				LineIterator lineIterator = IOUtils.lineIterator(stream, Charsets.UTF_8);

				while (lineIterator.hasNext()) {
					String line = lineIterator.next();

					LineType type = LineType.getLineType(line);

					IFilter includingFilter = null;
					IFilter excludingFilter = null;

					if (type != LineType.COMMENT) {
						if (type != null) {
							switch (type) {
							case BASIC:
								includingFilter = getBasicFilter(line);
								break;
							case EXTENDED:
								includingFilter = getExtendedFilter(line);
								break;
							case ELEMENT_HIDING:
								includingFilter = getHidingElementFilter(line);
								break;
							case EXCLUDING:
								String excludingRuleLine = line.replace("@@", StringUtils.EMPTY);
								excludingFilter = getBasicFilter(excludingRuleLine);
							default:
								// skip
								break;
							}
						}
						ruleCount++;
					}

					if (includingFilter != null) {
						mainRules.add(new CurrentTagRule(line, includingFilter));
					}
					if (excludingFilter != null) {
						excludingRules.add(new CurrentTagRule(line, excludingFilter));
					}
				}
			} catch (IOException e) {
				LOGGER.error("An error occured during reading Advertisment list file", e);

				throw e;
			}
		}

		LOGGER.info("It was loaded <" + (mainRules.size() + excludingRules.size()) + "> from <" + ruleCount + "> existing rule");

		includingRuleSet = mainRules.toArray(new IAdvertismentRule[mainRules.size()]);
		excludingRuleSet = excludingRules.toArray(new IAdvertismentRule[excludingRules.size()]);
	}

	protected IFilter getHidingElementFilter(final String line) {
		IFilterSource source = null;
		String pattern = null;

		int firstSharp = line.indexOf("#");
		if (firstSharp > 0) {
			String domains = line.substring(0, firstSharp);
			String rule = line.substring(firstSharp);

			IFilter domainFilter = createDomainFilter(domains, ",");
			IFilter ruleFilter = getHidingElementFilter(rule);

			if (ruleFilter != null) {
				return FilterBuilderUtils.joinAnd(domainFilter, ruleFilter);
			}
		} else {
			// check '>' or '+'

			pattern = getHidingElementPattern(line, "###");
			if (pattern != null) {
				source = FilterBuilderUtils.CSS_ID_FILTER_SOURCE;
			} else {
				pattern = getHidingElementPattern(line, "##*#");
				if (pattern != null) {
					source = FilterBuilderUtils.CSS_ID_FILTER_SOURCE;
				} else {
					pattern = getHidingElementPattern(line, "##.");
					if (pattern != null) {
						source = FilterBuilderUtils.CSS_SELECTOR_FILTER_SOURCE;
						pattern = "." + pattern;
					} else {
						pattern = getHidingElementPattern(line, "##");

						if (pattern != null) {
							if (pattern.contains(".")) {
								source = FilterBuilderUtils.CSS_SELECTOR_FILTER_SOURCE;
							} else {
								if (pattern.contains("#")) {
									source = FilterBuilderUtils.CSS_SELECTOR_FILTER_SOURCE;
									pattern = pattern.replace("#", ".");
								} else {
									source = FilterBuilderUtils.TAG_NAME_FILTER_SOURCE;
								}
							}

						}
					}
				}
			}
		}

		if ((source != null) && !StringUtils.isEmpty(pattern)) {
			int parametersIndex = pattern.indexOf("[");

			List<IFilter> filters = new ArrayList<>();

			if (parametersIndex > StringUtils.INDEX_NOT_FOUND) {
				String parameters = pattern.substring(parametersIndex);
				pattern = pattern.substring(0, parametersIndex);

				StringTokenizer tokenizer = new StringTokenizer(parameters, "[]", false);

				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();

					int attributeIndex = token.indexOf("=");

					String attribute = token.substring(0, attributeIndex);
					String value = token.substring(attributeIndex + 1);
					value = value.substring(1, value.length() - 1);

					IFilterSource attributeFilterSource = FilterBuilderUtils.getTagAttributeFilterSource(attribute);
					IFilter attributeFilter = FilterBuilderUtils.build(value, attributeFilterSource);

					filters.add(attributeFilter);
				}
			}

			filters.add(FilterBuilderUtils.build(pattern, source));

			return FilterBuilderUtils.joinAnd(filters);
		}

		return null;
	}

	private String getHidingElementPattern(final String line, final String prefix) {
		if (line.startsWith(prefix)) {
			return line.replace(prefix, StringUtils.EMPTY);
		}

		return null;
	}

	protected IFilter getExtendedFilter(final String line) {
		int parametersIndex = line.indexOf(PARAMETERS_SEPARATOR);

		String parametersBlock = line.substring(parametersIndex + 1);
		String baseBlock = line.substring(0, parametersIndex);

		IFilter parametersFilter = getParametersFilter(parametersBlock);
		IFilter basicFilter = getBasicFilter(baseBlock);

		if ((parametersFilter != null) && (basicFilter != null)) {
			return FilterBuilderUtils.joinAnd(parametersFilter, basicFilter);
		}

		return null;
	}

	protected IFilter getParametersFilter(final String parametersLine) {
		int rounds = StringUtils.countMatches(parametersLine, ",");

		if (rounds > 0) {
			String[] parameters = parametersLine.split(",");
			List<IFilter> filters = new ArrayList<>(parameters.length);

			for (String param : parameters) {
				IFilter subFilter = getParametersFilter(param);

				if (subFilter != null) {
					filters.add(subFilter);
				}
			}

			return FilterBuilderUtils.joinAnd(filters);
		}

		IFilter result = null;

		result = tryDomainFilter(parametersLine);
		if (result != null) {
			return result;
		}

		return result;
	}

	protected IFilter tryDomainFilter(final String parametersLine) {
		int domainIndex = parametersLine.indexOf(DOMAIN_PREFIX);

		if (domainIndex != StringUtils.INDEX_NOT_FOUND) {
			return createDomainFilter(parametersLine.substring(domainIndex + DOMAIN_PREFIX.length()), "|");
		}

		return null;
	}

	protected IFilter createDomainFilter(final String domainsLine, final String separator) {
		StringTokenizer tokenizer = new StringTokenizer(domainsLine, separator, false);

		IFilterSource filterSource = FilterBuilderUtils.DOMAIN_FILTER_SOURCE;

		List<IFilter> filters = new ArrayList<>();

		while (tokenizer.hasMoreTokens()) {
			filters.add(FilterBuilderUtils.build(tokenizer.nextToken(), filterSource, true));
		}

		return FilterBuilderUtils.joinAnd(filters);
	}

	protected IFilter getBasicFilter(final String line) {
		IFilterSource basicSource = FilterBuilderUtils.BASIC_FILTER_SOURCE;
		IFilter filter = FilterBuilderUtils.build(line, basicSource);

		return filter;
	}

	@Override
	public IAdvertismentRule[] getExcludingRules() {
		return excludingRuleSet;
	}
}
