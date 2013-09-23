package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilterSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 * 
 */
class TrueFilter extends AbstractFilter {

	public TrueFilter(final IFilterSource source) {
		super(source);
	}

	@Override
	protected boolean matches(final String text, final boolean isMatchCase) {
		return true;
	}

}
