package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
class MatchingFilter implements IFilter {

	private final String pattern;

	public MatchingFilter(final String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean matches(final String string) {
		return string.contains(pattern);
	}
}
