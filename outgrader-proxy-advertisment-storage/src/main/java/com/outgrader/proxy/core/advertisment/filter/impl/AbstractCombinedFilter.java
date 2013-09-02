package com.outgrader.proxy.core.advertisment.filter.impl;

import java.util.ArrayList;
import java.util.List;

import com.outgrader.proxy.core.advertisment.filter.IFilter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
abstract class AbstractCombinedFilter implements IFilter {

	private final List<IFilter> filters = new ArrayList<>();

	public void addSubFilter(final IFilter filter) {
		filters.add(filter);
	}

	protected List<IFilter> getFilters() {
		return filters;
	}

}
