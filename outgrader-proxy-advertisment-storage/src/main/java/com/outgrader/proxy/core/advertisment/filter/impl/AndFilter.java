package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
class AndFilter extends AbstractCombinedFilter {

	@Override
	public boolean matches(final String string) {
		for (IFilter filter : getFilters()) {
			if (!filter.matches(string)) {
				return false;
			}
		}

		return true;
	}

}
