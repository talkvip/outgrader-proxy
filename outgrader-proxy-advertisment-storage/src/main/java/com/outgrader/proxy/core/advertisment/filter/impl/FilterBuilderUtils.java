package com.outgrader.proxy.core.advertisment.filter.impl;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.advertisment.filter.IFilterSource;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public final class FilterBuilderUtils {

	private static final String NOT_SYMBOL = "~";

	private static final String TOKEN_SEPARATOR = "*";

	private static final String EDGE_SYMBOL = "|";

	private static final String PROTOCOL_SYMBOL = "||";

	private static final String SEPARATOR_SYMBOL = "^";

	private static final String[] DELIMETERS = { "//", "/", "?", "=", "&" };

	private interface IFilterBuilder {

		IFilter build(String rule, IFilterSource source, boolean supportsNot);

	}

	private static final IFilterBuilder NOT_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(final String rule, final IFilterSource source, final boolean supportsNot) {
			if (rule.contains(NOT_SYMBOL)) {
				return new NotFilter(MAIN_FILTER_BUILDER.build(rule.substring(1), source, supportsNot));
			}
			return null;
		}
	};

	private static final IFilterBuilder SEPARATOR_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(final String rule, final IFilterSource source, final boolean supportsNot) {
			int rounds = StringUtils.countMatches(rule, SEPARATOR_SYMBOL);
			if (rounds > 0) {

				OrFilter result = new OrFilter();

				for (String delimeter : DELIMETERS) {
					result.addSubFilter(build(rule.replaceFirst(Pattern.quote(SEPARATOR_SYMBOL), delimeter), source, supportsNot));
				}

				return result;
			}

			return new ContainsFilter(rule, source);
		}

	};

	private static final IFilterBuilder PROTOCOL_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(String rule, final IFilterSource source, final boolean supportsNot) {
			if (rule.startsWith(PROTOCOL_SYMBOL)) {
				IFilter subFilter = null;
				if (rule.contains(SEPARATOR_SYMBOL)) {
					subFilter = SEPARATOR_FILTER_BUILDER.build(rule.replace(PROTOCOL_SYMBOL, StringUtils.EMPTY), source, supportsNot);
				}

				if (subFilter != null) {
					rule = rule.replace(SEPARATOR_SYMBOL, StringUtils.EMPTY);
				}

				IFilter matchingFilter = new ContainsFilter(rule.replace(PROTOCOL_SYMBOL, "://"), source);

				if (subFilter == null) {
					return matchingFilter;
				} else {
					return joinAnd(matchingFilter, subFilter);
				}
			}

			return null;
		}
	};

	private static final IFilterBuilder ENDS_WITH_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(final String rule, final IFilterSource source, final boolean supportsNot) {
			if (rule.endsWith(EDGE_SYMBOL)) {
				return new ContainsFilter(rule.replace(EDGE_SYMBOL, "\""), source);
			}

			return null;
		}
	};

	private static final IFilterBuilder STARTS_WITH_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(final String rule, final IFilterSource source, final boolean supportsNot) {
			if (rule.startsWith(EDGE_SYMBOL)) {
				return new ContainsFilter(rule.replace(EDGE_SYMBOL, "\""), source);
			}

			return null;
		}
	};

	private static final IFilterBuilder MAIN_FILTER_BUILDER = new IFilterBuilder() {

		private final IFilterBuilder[] SUB_BUILDERS = new IFilterBuilder[] { PROTOCOL_FILTER_BUILDER, STARTS_WITH_FILTER_BUILDER,
				ENDS_WITH_FILTER_BUILDER, SEPARATOR_FILTER_BUILDER };

		@Override
		public IFilter build(final String rule, final IFilterSource source, final boolean supportsNot) {
			if (rule.contains(TOKEN_SEPARATOR)) {
				AndFilter result = new AndFilter();

				StringTokenizer tokenizer = new StringTokenizer(rule, TOKEN_SEPARATOR, false);
				while (tokenizer.hasMoreTokens()) {
					result.addSubFilter(build(tokenizer.nextToken(), source, supportsNot));
				}

				return result;
			}

			IFilter result = null;

			if (supportsNot) {
				result = NOT_FILTER_BUILDER.build(rule, source, supportsNot);
			}

			if (result == null) {
				for (IFilterBuilder builder : SUB_BUILDERS) {
					result = builder.build(rule, source, supportsNot);

					if (result != null) {
						break;
					}
				}
			}

			if (result == null) {
				result = new ContainsFilter(rule, source);
			}

			return result;
		}
	};

	public static final IFilterSource CSS_SELECTOR_FILTER_SOURCE = new AbstractFilterSource() {

		@Override
		public String getFilterSource(final String uri, final ITag tag) {
			return tag.getCSSId();
		}
	};

	public static final IFilterSource CSS_ID_FILTER_SOURCE = new AbstractFilterSource() {

		@Override
		public String getFilterSource(final String uri, final ITag tag) {
			return tag.getId();
		}
	};

	public static final IFilterSource TAG_NAME_FILTER_SOURCE = new AbstractFilterSource() {

		@Override
		public String getFilterSource(final String uri, final ITag tag) {
			return tag.getName();
		}
	};

	public static final IFilterSource BASIC_FILTER_SOURCE = new AbstractFilterSource() {

		@Override
		public String getFilterSource(final String uri, final ITag tag) {
			return tag.getText();
		}
	};

	public static final IFilterSource DOMAIN_FILTER_SOURCE = new AbstractFilterSource() {

		@Override
		public String getFilterSource(final String uri, final ITag tag) {
			return uri;
		}
	};

	public static IFilterSource getTagAttributeFilterSource(final String attributeName) {
		return new AbstractFilterSource() {

			@Override
			public String getFilterSource(final String uri, final ITag tag) {
				return tag.getAttribute(attributeName);
			}
		};
	}

	public static IFilter build(final String rule, final IFilterSource filterSource) {
		return build(rule, filterSource, false);
	}

	public static IFilter build(final String rule, final IFilterSource filterSource, final boolean supportsNot) {
		return MAIN_FILTER_BUILDER.build(rule, filterSource, supportsNot);
	}

	public static IFilter buildStartsWithFilter(final String rule, final IFilterSource filterSource) {
		return new StartsWithFilter(rule, filterSource);
	}

	public static IFilter buildEndsWithFilter(final String rule, final IFilterSource filterSource) {
		return new EndsWithFilter(rule, filterSource);
	}

	public static IFilter joinAnd(final IFilter... filters) {
		if (filters.length == 1) {
			return filters[0];
		}

		AndFilter result = new AndFilter();

		for (IFilter filter : filters) {
			result.addSubFilter(filter);
		}

		return result;
	}

	public static IFilter joinAnd(final List<IFilter> filters) {
		return joinAnd(filters.toArray(new IFilter[filters.size()]));
	}

}
