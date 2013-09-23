package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.advertisment.filter.IFilterSource;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 * 
 */
class ContainsDomainFilter implements IFilter {

	private final IFilterSource source;

	public ContainsDomainFilter(final IFilterSource source) {
		this.source = source;
	}

	@Override
	public boolean matches(final String uri, final ITag tag) {
		String tagSource = source.getFilterSource(uri, tag);

		if (tagSource != null) {
			return tagSource.contains(uri);
		}
		return false;
	}

}
