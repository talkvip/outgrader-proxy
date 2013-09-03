package com.outgrader.proxy.core.advertisment.filter.impl;

import org.apache.commons.lang3.StringUtils;

import com.outgrader.proxy.core.advertisment.filter.IFilterSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
class MatchingFilter extends AbstractFilter {

	private final String pattern;

	public MatchingFilter(final String pattern, final IFilterSource source) {
		super(source);
		this.pattern = pattern;
	}

	@Override
	public boolean matches(final String text, final boolean isMatchCase) {
		if (isMatchCase) {
			return StringUtils.containsIgnoreCase(text, pattern);
		}

		return text.contains(pattern);
	}
}
