package com.outgrader.proxy.core.advertisment.filter.impl;

import org.apache.commons.lang3.StringUtils;

import com.outgrader.proxy.core.advertisment.filter.IFilterSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 * 
 */
public class EndsWithFilter extends AbstractFilter {

	private final String pattern;

	public EndsWithFilter(final String pattern, final IFilterSource source) {
		super(source);
		this.pattern = pattern;
	}

	@Override
	public boolean matches(final String text, final boolean isMatchCase) {
		if (isMatchCase) {
			return StringUtils.endsWithIgnoreCase(text, pattern);
		}

		return text.endsWith(pattern);
	}

}
