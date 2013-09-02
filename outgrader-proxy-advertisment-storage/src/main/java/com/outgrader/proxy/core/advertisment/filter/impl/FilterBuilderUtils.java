package com.outgrader.proxy.core.advertisment.filter.impl;

import java.util.StringTokenizer;

import com.outgrader.proxy.core.advertisment.filter.IFilter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public final class FilterBuilderUtils {

	private static final String TOKEN_SEPARATOR = "*";

	private static final String EDGE_SYMBOL = "|";

	private static final String PROTOCOL_SYMBOL = "||";

	private interface IFilterBuilder {

		IFilter build(String rule);

	}

	private static final IFilterBuilder PROTOCOL_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(final String rule) {
			if (rule.startsWith(PROTOCOL_SYMBOL)) {
				return new MatchingFilter(rule.replace(PROTOCOL_SYMBOL, "://"));
			}

			return null;
		}
	};

	private static final IFilterBuilder ENDS_WITH_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(final String rule) {
			if (rule.endsWith(EDGE_SYMBOL)) {
				return new MatchingFilter(rule.replace(EDGE_SYMBOL, "\""));
			}

			return null;
		}
	};

	private static final IFilterBuilder STARTS_WITH_FILTER_BUILDER = new IFilterBuilder() {

		@Override
		public IFilter build(final String rule) {
			if (rule.startsWith(EDGE_SYMBOL)) {
				return new MatchingFilter(rule.replace(EDGE_SYMBOL, "\""));
			}

			return null;
		}
	};

	private static final IFilterBuilder MAIN_FILTER_BUILDER = new IFilterBuilder() {

		private final IFilterBuilder[] SUB_BUILDERS = new IFilterBuilder[] { PROTOCOL_FILTER_BUILDER, STARTS_WITH_FILTER_BUILDER, ENDS_WITH_FILTER_BUILDER };

		@Override
		public IFilter build(final String rule) {
			if (rule.contains(TOKEN_SEPARATOR)) {
				AndFilter result = new AndFilter();

				StringTokenizer tokenizer = new StringTokenizer(rule, TOKEN_SEPARATOR, false);
				while (tokenizer.hasMoreTokens()) {
					result.addSubFilter(build(tokenizer.nextToken()));
				}

				return result;
			}

			IFilter result = null;

			for (IFilterBuilder builder : SUB_BUILDERS) {
				result = builder.build(rule);

				if (result != null) {
					break;
				}
			}

			if (result == null) {
				result = new MatchingFilter(rule);
			}

			return result;
		}
	};

	public static IFilter build(final String rule) {
		return MAIN_FILTER_BUILDER.build(rule);
	}

}
