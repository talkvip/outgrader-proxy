package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.advertisment.filter.IFilterSource;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
abstract class AbstractFilter implements IFilter {

	private final IFilterSource source;

	protected AbstractFilter(final IFilterSource source) {
		this.source = source;
	}

	@Override
	public boolean matches(final String uri, final ITag tag) {
		String value = source.getFilterSource(uri, tag);

		if (value != null) {
			return matches(value, source.isMatchCase());
		}

		return false;
	}

	protected abstract boolean matches(String text, boolean isMatchCase);

}
