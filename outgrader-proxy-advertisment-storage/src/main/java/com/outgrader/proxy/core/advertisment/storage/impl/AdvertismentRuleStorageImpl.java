package com.outgrader.proxy.core.advertisment.storage.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvertismentRuleStorageImpl.class);

	private static final String DOMAIN_PREFIX = "domain=";

	private static final String PARAMETERS_SEPARATOR = "$";

	private static class FilterResult {

		private final IFilter filter;

		private final List<IFilter> subRules;

		private final List<String> domains;

		public FilterResult(final IFilter filter) {
			this(filter, new ArrayList<String>(0));
		}

		public FilterResult(final IFilter filter, final List<String> domains) {
			this(filter, domains, new ArrayList<IFilter>(0));
		}

		public FilterResult(final IFilter filter, final List<String> domains, final List<IFilter> subRules) {
			this.filter = filter;
			this.domains = domains;
			this.subRules = subRules;
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

		public static FilterResult mergeAnd(final FilterResult first, final FilterResult second) {
			List<String> domains = first.domains;
			domains.addAll(second.domains);

			List<IFilter> subRules = first.subRules;
			subRules.addAll(second.subRules);

			IFilter filter = first.getFilter();
			if (filter == null) {
				filter = second.getFilter();
			} else {
				if (second.getFilter() != null) {
					filter = FilterBuilderUtils.joinAnd(filter, second.getFilter());
				}
			}

			return new FilterResult(filter, domains, subRules);
		}
	}

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

	private final AdvertismentRuleVault includingRulesVault = new AdvertismentRuleVault();

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

					FilterResult includingFilterResult = null;
					FilterResult excludingFilterResult = null;

					if (type != LineType.COMMENT) {
						switch (type) {
						case BASIC:
							includingFilterResult = getBasicFilter(line);
							break;
						case ELEMENT_HIDING:
							if (!line.contains(" + ")) {
								boolean first = true;
								for (String component : line.split(" > ")) {
									if (!first) {
										component = "##" + component.trim();
									}

									FilterResult componentResult = getHidingElementFilter(component.trim(), first);

									first = false;

									if (includingFilterResult == null) {
										includingFilterResult = componentResult;
									} else {
										includingFilterResult = FilterResult.mergeAnd(includingFilterResult, componentResult);
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
						ruleCount++;
					}

					if (excludingFilterResult != null) {
						excludingRules.add(new AdvertismentRuleImpl(line, excludingFilterResult.getFilter()));
					}
					if (includingFilterResult != null) {
						AdvertismentRuleImpl rule = new AdvertismentRuleImpl(line, includingFilterResult.getFilter());
						for (IFilter filter : includingFilterResult.getSubRules()) {
							rule.addSubRule(new AdvertismentRuleImpl(line, filter));
						}

						mainRules.add(rule);

						for (AdvertismentRuleVault vault : getVaults(includingFilterResult)) {
							vault.addRule(rule);
						}
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

	private List<AdvertismentRuleVault> getVaults(final FilterResult filterResult) {
		List<AdvertismentRuleVault> result = new ArrayList<>();

		for (String domain : filterResult.getDomains()) {
			AdvertismentRuleVault domainVault = includingRulesVault.createSubVault(domain);

			result.add(domainVault);
		}

		if (result.isEmpty()) {
			result.add(includingRulesVault);
		}

		return result;
	}

	private FilterResult getHidingElementFilter(final String line, final boolean isMainFilter) {

		return null;
	}

	private FilterResult getExtendedFilter(final String line) {
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

	private FilterResult getParametersFilter(final String line) {
		FilterResult result = null;

		for (String parameter : line.split(",")) {
			FilterResult subResult = null;

			subResult = tryDomainFilter(parameter);
			if (subResult == null) {
				// subResult =
			}

			if (result == null) {
				result = subResult;
			} else if (subResult != null) {
				result = FilterResult.mergeAnd(result, subResult);
			}
		}

		return result;
	}

	private FilterResult tryDomainFilter(final String parameterLine) {
		int domainIndex = parameterLine.indexOf(DOMAIN_PREFIX);

		List<String> domains = new ArrayList<>();
		List<IFilter> excludingDomains = new ArrayList<>();

		if (domainIndex != StringUtils.INDEX_NOT_FOUND) {
			String domainsList = parameterLine.substring(domainIndex + DOMAIN_PREFIX.length());

			for (String domain : domainsList.split("\\|")) {
				if (domain.startsWith("~")) {
					IFilterSource filterSource = FilterBuilderUtils.DOMAIN_FILTER_SOURCE;
					IFilter domainFilter = FilterBuilderUtils.buildContainsFilter(domain, filterSource);
					domainFilter = FilterBuilderUtils.not(domainFilter);

					excludingDomains.add(domainFilter);
				} else {
					domains.add(domain);
				}
			}

			return new FilterResult(FilterBuilderUtils.joinAnd(excludingDomains), domains);
		}

		return null;
	}

	private FilterResult getBasicFilter(final String line) {
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
