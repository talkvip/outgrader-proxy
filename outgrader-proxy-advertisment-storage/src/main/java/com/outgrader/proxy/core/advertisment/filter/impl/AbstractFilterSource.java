package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilterSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public abstract class AbstractFilterSource implements IFilterSource {

	private final boolean isMatchCase;

	protected AbstractFilterSource() {
		this(false);
	}

	protected AbstractFilterSource(final boolean isMatchCase) {
		this.isMatchCase = isMatchCase;
	}

	@Override
	public boolean isMatchCase() {
		return isMatchCase;
	}

}
