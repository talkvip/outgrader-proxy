package com.outgrader.proxy.core.advertisment.filter.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.ArrayUtils;

import com.outgrader.proxy.core.advertisment.filter.IFilter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
abstract class AbstractCombinedFilter implements IFilter {

	private IFilter[] filters = new IFilter[0];

	public void addSubFilter(final IFilter filter) {
		checkNotNull(filter, "Sub-filter cannot be null");

		filters = ArrayUtils.add(filters, filter);
	}

	protected IFilter[] getFilters() {
		return filters;
	}

}
