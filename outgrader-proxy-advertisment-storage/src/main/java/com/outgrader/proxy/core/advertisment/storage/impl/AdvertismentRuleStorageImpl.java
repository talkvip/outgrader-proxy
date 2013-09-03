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
import com.outgrader.proxy.core.advertisment.rule.impl.internal.AdvertismentRuleImpl;
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
		COMMENT("!"), BASIC(null), ELEMENT_HIDING("#"), EXCLUDING("@@"), EXTENDED(PARAMETERS_SEPARATOR);

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

	private IAdvertismentRule[] ruleSet;

	@Inject
	public AdvertismentRuleStorageImpl(final IOutgraderProperties properties) throws Exception {
		this.properties = properties;
	}

	@Override
	public IAdvertismentRule[] getRules() {
		return ruleSet;
	}

	protected InputStream openRuleFileStream(final String location) {
		return AdvertismentRuleStorageImpl.class.getResourceAsStream(location);
	}

	@PostConstruct
	protected void initializeRuleSet() throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("started initializeRuleSet()");
		}

		Collection<IAdvertismentRule> result = new ArrayList<>();

		for (String location : properties.getAdvertismentListLocations()) {
			LOGGER.info("Initializing rule set from <" + location + ">");

			try (InputStream stream = openRuleFileStream(location)) {
				LineIterator lineIterator = IOUtils.lineIterator(stream, Charsets.UTF_8);

				while (lineIterator.hasNext()) {
					String line = lineIterator.next();

					LineType type = LineType.getLineType(line);

					IFilter filter = null;

					if (type != null) {
						switch (type) {
						case BASIC:
							filter = getBasicFilter(line);
							break;
						case EXTENDED:
							filter = getExtendedFilter(line);
							break;
						case ELEMENT_HIDING:
							filter = getHidingElementFilter(line);
							break;
						default:
							// skip
							break;
						}
					}

					if (filter != null) {
						result.add(new AdvertismentRuleImpl(line, filter));
					}
				}
			} catch (IOException e) {
				LOGGER.error("An error occured during reading Advertisment list file", e);

				throw e;
			}
		}

		LOGGER.info("It was loaded <" + result.size() + "> rules");

		ruleSet = result.toArray(new IAdvertismentRule[result.size()]);
	}

	protected IFilter getHidingElementFilter(final String line) {
		IFilterSource source = null;
		String pattern = null;

		pattern = getHidingElementPattern(line, "###");
		if (pattern != null) {
			source = FilterBuilderUtils.getCSSIdFilterSource();
		} else {
			pattern = getHidingElementPattern(line, "##*#");
			if (pattern != null) {
				source = FilterBuilderUtils.getCSSIdFilterSource();
			} else {
				pattern = getHidingElementPattern(line, "##.");
				if (pattern != null) {
					source = FilterBuilderUtils.getCSSSelectorFilterSource();
					pattern = "." + pattern;
				} else {
					pattern = getHidingElementPattern(line, "##");

					if (pattern != null) {
						if (!pattern.contains("[")) {
							if (pattern.contains(".")) {
								source = FilterBuilderUtils.getCSSSelectorFilterSource();
							} else {
								if (pattern.contains("#")) {
									source = FilterBuilderUtils.getCSSSelectorFilterSource();
									pattern = pattern.replace("#", ".");
								} else {
									source = FilterBuilderUtils.getTagNameFilterSource();
								}
							}
						}
					}
				}
			}
		}

		if ((source != null) && !StringUtils.isEmpty(pattern)) {
			return FilterBuilderUtils.build(pattern, source);
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
			StringTokenizer tokenizer = new StringTokenizer(parametersLine.substring(domainIndex + DOMAIN_PREFIX.length()), "|", false);

			IFilterSource filterSource = FilterBuilderUtils.getDomainFilterSource();

			List<IFilter> filters = new ArrayList<>();

			while (tokenizer.hasMoreTokens()) {
				filters.add(FilterBuilderUtils.build(tokenizer.nextToken(), filterSource, true));
			}

			return FilterBuilderUtils.joinAnd(filters);
		}

		return null;
	}

	protected IFilter getBasicFilter(final String line) {
		IFilterSource basicSource = FilterBuilderUtils.getBasicFilterSource();
		IFilter filter = FilterBuilderUtils.build(line, basicSource);

		return filter;
	}
}
