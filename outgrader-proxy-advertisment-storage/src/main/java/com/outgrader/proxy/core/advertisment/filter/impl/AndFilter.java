package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
class AndFilter extends AbstractCombinedFilter {

	@Override
	public boolean matches(final String uri, final ITag tag) {
		for (IFilter filter : getFilters()) {
			if (!filter.matches(uri, tag)) {
				return false;
			}
		}

		return true;
	}

}
