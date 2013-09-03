package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
class NotFilter implements IFilter {

	private final IFilter source;

	public NotFilter(final IFilter source) {
		this.source = source;
	}

	@Override
	public boolean matches(final String string) {
		return !source.matches(string);
	}

}
