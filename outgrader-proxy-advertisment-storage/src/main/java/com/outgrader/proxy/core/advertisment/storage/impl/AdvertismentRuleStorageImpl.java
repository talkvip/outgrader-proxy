package com.outgrader.proxy.core.advertisment.storage.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.outgrader.proxy.core.advertisment.rule.impl.internal.AdvertismentRuleImpl;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage;
import com.outgrader.proxy.core.storage.IAdvertismentRuleVault;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Component
public class AdvertismentRuleStorageImpl implements IAdvertismentRuleStorage {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AdvertismentRuleStorageImpl.class);

	private static final char NAME_VALUE_SEPARATOR = '=';

	private static final String ATTRIBUTE_PART_SEPARATORS = "[]";

	private static final char CSS_PARTS_SEPARATOR = '.';

	private static final String CSS_SELECTOR_PREFIX = "##.";

	private static final String CSS_ID_PREFIX_2 = "##*#";

	private static final String CSS_ID_PREFIX_1 = "###";

	private static final char START_ATTR_CHARACTER = '[';

	private static final String NOT_PREFIX = "~";

	private static final String HIDING_ELEMENT_DOMAINS_SEPARATOR = ",";

	private static final char DOMAIN_PART_SEPARATOR = '#';

	private static final String HIDING_RULE_PREFIX = "##";

	private static final String CHILD_SEPARATOR = " > ";

	private static final String NEXT_SEPARATOR = " + ";

	private static final String DOMAIN_PREFIX = "domain=";

	private static final String EXTENDED_FILTER_DOMAIN_SEPARATOR = "\\|";

	private static final String ENDS_WITH_FILTER = "$";

	private static final String STARTS_WITH_PREFIX = "^";

	private static final String ANY_POSTFIX = "*";

	private static final char PARAMETERS_SEPARATOR = '$';

	private static final String[] EXTENDED_FILTER_TAG_NAMES = { "object",
			"script" };

	private static class FilterResult {

		private final IFilter filter;

		private final List<IFilter> subRules;

		private final List<String> domains;

		private final List<String> tags = new ArrayList<>();

		public FilterResult(final IFilter filter) {
			this(filter, new ArrayList<String>(0));
		}

		public FilterResult(final IFilter filter, final String tag) {
			this(filter, new ArrayList<String>(0), new ArrayList<IFilter>(0),
					tag);
		}

		public FilterResult(final IFilter filter, final List<String> domains) {
			this(filter, domains, new ArrayList<IFilter>(0));
		}

		public FilterResult(final IFilter filter, final List<String> domains,
				final List<IFilter> subRules) {
			this(filter, domains, subRules, (String) null);
		}

		public FilterResult(final IFilter filter, final List<String> domains,
				final List<IFilter> subRules, final String tag) {
			this.filter = filter;
			this.domains = domains;
			this.subRules = subRules;
			if (tag != null) {
				this.tags.add(tag);
			}
		}

		public FilterResult(final IFilter filter, final List<String> domains,
				final List<IFilter> subRules, final List<String> tags) {
			this(filter, domains, subRules);
			this.tags.addAll(tags);
		}

		public List<String> getDomains() {
			return domains;
		}

		public IFilter getFilter() {
			return filter;
		}

		public List<IFilter> getSubRules() {
			return subRules;
		}

		public List<String> getTags() {
			return tags;
		}

		public static FilterResult mergeAnd(final FilterResult first,
				final FilterResult second) {
			List<String> domains = first.domains;
			domains.addAll(second.domains);

			List<IFilter> subRules = first.subRules;
			subRules.addAll(second.subRules);

			IFilter filter = first.getFilter();
			if (filter == null) {
				filter = second.getFilter();
			} else {
				if (second.getFilter() != null) {
					filter = FilterBuilderUtils.joinAnd(filter,
							second.getFilter());
				}
			}

			List<String> tags = first.getTags();
			tags.addAll(second.getTags());

			return new FilterResult(filter, domains, subRules, tags);
		}
	}

	private enum LineType {
		COMMENT("!", true), BASIC(null), ELEMENT_HIDING(HIDING_RULE_PREFIX), EXCLUDING(
				"@@", true), EXTENDED(PARAMETERS_SEPARATOR);

		private String symbol;

		private boolean shouldStart;

		private LineType(final String symbol) {
			this(symbol, false);
		}

		private LineType(final char character) {
			this(Character.toString(character));
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

	private IAdvertismentRule[] excludingRuleSet;

	private final AdvertismentRuleVault includingRulesVault = new AdvertismentRuleVault();

	@Inject
	public AdvertismentRuleStorageImpl(final IOutgraderProperties properties)
			throws Exception {
		this.properties = properties;
	}

	protected InputStream openRuleFileStream(final String location) {
		return AdvertismentRuleStorageImpl.class.getResourceAsStream(location);
	}

	@PostConstruct
	protected void initializeRuleSet() throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("started initializeRuleSet()");
		}

		Collection<IAdvertismentRule> excludingRules = new ArrayList<>();

		int lineCount = 0;
		int ruleCount = 0;

		for (String location : properties.getAdvertismentListLocations()) {
			LOGGER.info("Initializing rule set from <" + location + ">");

			try (InputStream stream = openRuleFileStream(location)) {
				LineIterator lineIterator = IOUtils.lineIterator(stream,
						Charsets.UTF_8);

				while (lineIterator.hasNext()) {
					String line = lineIterator.next();

					try {
						LineType type = LineType.getLineType(line);

						FilterResult includingFilterResult = null;
						FilterResult excludingFilterResult = null;

						if (type != LineType.COMMENT) {
							switch (type) {
							case BASIC:
								includingFilterResult = getBasicFilter(line);
								break;
							case ELEMENT_HIDING:
								if (!line.contains(NEXT_SEPARATOR)) {
									boolean first = true;
									for (String component : line
											.split(CHILD_SEPARATOR)) {
										if (!first) {
											component = HIDING_RULE_PREFIX
													+ component.trim();
										}

										FilterResult componentResult = getHidingElementFilter(
												component.trim(), first);

										first = false;

										if (includingFilterResult == null) {
											includingFilterResult = componentResult;
										} else {
											includingFilterResult = FilterResult
													.mergeAnd(
															includingFilterResult,
															componentResult);
										}
									}
								}
								break;
							case EXCLUDING:
								excludingFilterResult = getBasicFilter(line);
								break;
							case EXTENDED:
								includingFilterResult = getExtendedFilter(line);
								break;
							default:
								break;
							}
							lineCount++;
						}

						if (excludingFilterResult != null) {
							excludingRules.add(new AdvertismentRuleImpl(line,
									excludingFilterResult.getFilter()));
							ruleCount++;
						}
						if (includingFilterResult != null) {
							AdvertismentRuleImpl rule = new AdvertismentRuleImpl(
									line, includingFilterResult.getFilter());
							for (IFilter filter : includingFilterResult
									.getSubRules()) {
								rule.addSubRule(new AdvertismentRuleImpl(line,
										filter));
							}

							for (AdvertismentRuleVault vault : getVaults(includingFilterResult)) {
								vault.addRule(rule);
							}
							ruleCount++;

							if ((lineCount % 1000) == 0) {
								LOGGER.debug("It was processed <" + lineCount
										+ "> rules.");
							}
						}
					} catch (Exception e) {
						LOGGER.error(
								"An error occured during processing rule <"
										+ line + ">", e);

						throw e;
					}
				}
			} catch (IOException e) {
				LOGGER.error(
						"An error occured during reading Advertisment list file",
						e);

				throw e;
			}
		}

		includingRulesVault.close();

		LOGGER.info("It was loaded <" + ruleCount + "> from <" + lineCount
				+ "> existing rule");

		excludingRuleSet = excludingRules
				.toArray(new IAdvertismentRule[excludingRules.size()]);
	}

	private List<AdvertismentRuleVault> getVaults(
			final FilterResult filterResult) {
		List<AdvertismentRuleVault> result = new ArrayList<>();

		for (String domain : filterResult.getDomains()) {
			AdvertismentRuleVault domainVault = includingRulesVault
					.createSubVault(domain);

			for (String tag : filterResult.getTags()) {
				AdvertismentRuleVault tagVault = domainVault
						.createSubVault(tag);

				result.add(tagVault);
			}

			result.add(domainVault);
		}

		if (result.isEmpty()) {
			for (String tag : filterResult.getTags()) {
				AdvertismentRuleVault tagVault = includingRulesVault
						.createSubVault(tag);

				result.add(tagVault);
			}
		}

		if (result.isEmpty()) {
			result.add(includingRulesVault);
		}

		return result;
	}

	protected FilterResult getHidingElementFilter(final String line,
			final boolean isMainFilter) {
		String current = line;
		int domainsPartIndex = current.indexOf(DOMAIN_PART_SEPARATOR);

		List<String> domains = new ArrayList<>();
		IFilter domainFilter = null;

		if (domainsPartIndex > 0) {
			String domainPart = current.substring(0, domainsPartIndex);
			current = current.substring(domainsPartIndex);

			List<IFilter> filters = new ArrayList<>();

			IFilterSource filterSource = FilterBuilderUtils.DOMAIN_FILTER_SOURCE;

			for (String domain : domainPart
					.split(HIDING_ELEMENT_DOMAINS_SEPARATOR)) {
				if (domain.startsWith(NOT_PREFIX)) {
					filters.add(FilterBuilderUtils.build(domain, filterSource));
				} else {
					domains.add(domain);
				}
			}

			domainFilter = FilterBuilderUtils.joinAnd(filters);
		}

		int attributesPartIndex = current.indexOf(START_ATTR_CHARACTER);

		String cssPart = null;
		String attributePart = null;

		if (attributesPartIndex != StringUtils.INDEX_NOT_FOUND) {
			cssPart = current.substring(0, attributesPartIndex);
			attributePart = current.substring(attributesPartIndex);
		} else {
			cssPart = current;
		}

		IFilterSource cssFilterSource = null;
		String cssFilterBase = null;
		String tagName = null;
		boolean shouldBeEquals = false;

		if (cssPart.startsWith(CSS_ID_PREFIX_1)
				|| cssPart.startsWith(CSS_ID_PREFIX_2)) {
			cssFilterSource = FilterBuilderUtils.CSS_ID_FILTER_SOURCE;
			cssFilterBase = cssPart.replaceAll(CSS_ID_PREFIX_1,
					StringUtils.EMPTY).replaceAll(CSS_ID_PREFIX_2,
					StringUtils.EMPTY);
			shouldBeEquals = true;
		} else if (cssPart.startsWith(CSS_SELECTOR_PREFIX)) {
			cssFilterSource = FilterBuilderUtils.CSS_SELECTOR_FILTER_SOURCE;
			cssFilterBase = cssPart.replace(HIDING_RULE_PREFIX,
					StringUtils.EMPTY);
		} else {
			int cssIdPartIndex = cssPart.indexOf(CSS_PARTS_SEPARATOR);

			if (cssIdPartIndex != StringUtils.INDEX_NOT_FOUND) {
				tagName = cssPart.substring(2, cssIdPartIndex);
				cssFilterBase = cssPart.substring(cssIdPartIndex);
			} else {
				tagName = cssPart
						.replace(HIDING_RULE_PREFIX, StringUtils.EMPTY);
			}
		}

		IFilter cssFilter = null;
		if ((cssFilterBase != null) && (cssFilterSource != null)) {
			if (shouldBeEquals) {
				cssFilter = FilterBuilderUtils.buildEqualsFilter(cssFilterBase,
						cssFilterSource);
			} else {
				cssFilter = FilterBuilderUtils.buildContainsFilter(
						cssFilterBase, cssFilterSource);
			}
		}

		List<IFilter> attributeFilters = new ArrayList<>();

		if (!StringUtils.isEmpty(attributePart)) {
			StringTokenizer tokenizer = new StringTokenizer(attributePart,
					ATTRIBUTE_PART_SEPARATORS, false);

			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();

				int attributeIndex = token.indexOf(NAME_VALUE_SEPARATOR);

				IFilter attributeFilter = null;

				if (attributeIndex != StringUtils.INDEX_NOT_FOUND) {
					String attribute = token.substring(0, attributeIndex);
					String value = token.substring(attributeIndex + 1);
					value = value.substring(1, value.length() - 1);

					IFilterSource attributeFilterSource = FilterBuilderUtils
							.getTagAttributeFilterSource(attribute);

					if (attribute.endsWith(ANY_POSTFIX)) {
						attribute = attribute.replace(ANY_POSTFIX,
								StringUtils.EMPTY);

						attributeFilterSource = FilterBuilderUtils
								.getTagAttributeFilterSource(attribute);

						attributeFilter = FilterBuilderUtils
								.buildContainsFilter(value,
										attributeFilterSource);
					} else if (attribute.endsWith(STARTS_WITH_PREFIX)) {
						attribute = attribute.replace(STARTS_WITH_PREFIX,
								StringUtils.EMPTY);

						attributeFilterSource = FilterBuilderUtils
								.getTagAttributeFilterSource(attribute);

						attributeFilter = FilterBuilderUtils
								.buildStartsWithFilter(value,
										attributeFilterSource);
					} else if (attribute.endsWith(ENDS_WITH_FILTER)) {
						attribute = attribute.replace(ENDS_WITH_FILTER,
								StringUtils.EMPTY);

						attributeFilterSource = FilterBuilderUtils
								.getTagAttributeFilterSource(attribute);

						attributeFilter = FilterBuilderUtils
								.buildEndsWithFilter(value,
										attributeFilterSource);
					} else {
						attributeFilter = FilterBuilderUtils.buildEqualsFilter(
								value, attributeFilterSource);
					}
				} else {
					IFilterSource attributeFilterSource = FilterBuilderUtils
							.getTagAttributeFilterSource(token);
					attributeFilter = FilterBuilderUtils
							.buildTrueFilter(attributeFilterSource);
				}

				attributeFilters.add(attributeFilter);
			}
		}

		if (cssFilter != null) {
			attributeFilters.add(cssFilter);
			Collections.rotate(attributeFilters, 1);
		}

		if (domainFilter != null) {
			attributeFilters.add(domainFilter);
			Collections.rotate(attributeFilters, 1);
		}

		IFilter mainFilter = FilterBuilderUtils.joinAnd(attributeFilters);

		if (isMainFilter) {
			return new FilterResult(mainFilter, domains,
					new ArrayList<IFilter>(), tagName);
		} else {
			List<IFilter> filters = new ArrayList<>();
			filters.add(mainFilter);

			return new FilterResult(null, domains, filters, tagName);
		}
	}

	protected FilterResult getExtendedFilter(final String line) {
		int parametersIndex = line.indexOf(PARAMETERS_SEPARATOR);

		String parametersBlock = line.substring(parametersIndex + 1);
		String baseBlock = line.substring(0, parametersIndex);

		FilterResult basicFilter = getBasicFilter(baseBlock);
		FilterResult parametersFilter = getParametersFilter(parametersBlock);

		if (parametersFilter != null) {
			return FilterResult.mergeAnd(parametersFilter, basicFilter);
		}

		return null;
	}

	protected FilterResult getParametersFilter(final String line) {
		FilterResult result = null;

		for (String parameter : line.split(HIDING_ELEMENT_DOMAINS_SEPARATOR)) {
			FilterResult subResult = null;

			subResult = tryDomainFilter(parameter);
			if (subResult == null) {
				subResult = tryThirdPartyFilter(parameter);

				if (subResult == null) {
					for (String tagName : EXTENDED_FILTER_TAG_NAMES) {
						subResult = tryExtendedTagNameParameterFilter(
								parameter, tagName);

						if (subResult != null) {
							break;
						}
					}
				}
			}

			if (result == null) {
				result = subResult;
			} else if (subResult != null) {
				result = FilterResult.mergeAnd(result, subResult);
			}
		}

		return result;
	}

	private FilterResult tryThirdPartyFilter(final String parametersLine) {
		if (parametersLine.contains("third-party")) {
			IFilterSource filterSource = FilterBuilderUtils.BASIC_FILTER_SOURCE;

			IFilter filter = FilterBuilderUtils
					.buildContainsDomainFilter(filterSource);

			if (!parametersLine.contains(NOT_PREFIX)) {
				filter = FilterBuilderUtils.not(filter);
			}

			return new FilterResult(filter);
		}

		return null;
	}

	private FilterResult tryDomainFilter(final String parameterLine) {
		int domainIndex = parameterLine.indexOf(DOMAIN_PREFIX);

		List<String> domains = new ArrayList<>();
		List<IFilter> excludingDomains = new ArrayList<>();

		if (domainIndex != StringUtils.INDEX_NOT_FOUND) {
			String domainsList = parameterLine.substring(domainIndex
					+ DOMAIN_PREFIX.length());

			for (String domain : domainsList
					.split(EXTENDED_FILTER_DOMAIN_SEPARATOR)) {
				if (domain.startsWith(NOT_PREFIX)) {
					IFilterSource filterSource = FilterBuilderUtils.DOMAIN_FILTER_SOURCE;
					IFilter domainFilter = FilterBuilderUtils
							.buildContainsFilter(domain, filterSource);
					domainFilter = FilterBuilderUtils.not(domainFilter);

					excludingDomains.add(domainFilter);
				} else {
					domains.add(domain);
				}
			}

			return new FilterResult(
					FilterBuilderUtils.joinAnd(excludingDomains), domains);
		}

		return null;
	}

	private FilterResult tryExtendedTagNameParameterFilter(
			final String parameterLine, final String tagName) {
		if (parameterLine.contains(tagName)) {
			IFilterSource filterSource = FilterBuilderUtils.TAG_NAME_FILTER_SOURCE;
			IFilter filter = null;
			String tag = null;

			if (parameterLine.startsWith(NOT_PREFIX)) {
				filter = FilterBuilderUtils.buildEqualsFilter(tagName,
						filterSource);
				filter = FilterBuilderUtils.not(filter);
			} else {
				tag = tagName;
			}

			return new FilterResult(filter, tag);
		}

		return null;
	}

	protected FilterResult getBasicFilter(final String line) {
		IFilterSource filterSource = FilterBuilderUtils.BASIC_FILTER_SOURCE;
		IFilter filter = FilterBuilderUtils.build(line, filterSource);

		return new FilterResult(filter);
	}

	@Override
	public IAdvertismentRule[] getExcludingRules() {
		return excludingRuleSet;
	}

	@Override
	public IAdvertismentRuleVault getIncludingRulesVault() {
		return includingRulesVault;
	}
}
