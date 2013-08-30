package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.advertisment.filter.IFilterBuilder;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public class FilterBuilderImpl implements IFilterBuilder {

	private static final String TOKEN_SEPARATOR = "*";

	@Override
	public IFilter build(final String rule) {
		AndFilter result = new AndFilter();

		if (rule.contains(TOKEN_SEPARATOR)) {
			for (String subToken : rule.split(TOKEN_SEPARATOR)) {
				result.addSubFilter(build(subToken));
			}
		}

		return result;
	}

}
