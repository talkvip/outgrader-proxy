package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.model.ITag;

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
	public boolean matches(final String uri, final ITag tag) {
		return !source.matches(uri, tag);
	}

}
