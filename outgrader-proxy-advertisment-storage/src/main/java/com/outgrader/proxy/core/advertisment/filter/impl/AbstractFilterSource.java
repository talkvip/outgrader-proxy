package com.outgrader.proxy.core.advertisment.filter.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilterSource;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public abstract class AbstractFilterSource implements IFilterSource {

	private String value = null;

	private final boolean isMatchCase;

	protected AbstractFilterSource() {
		this(false);
	}

	protected AbstractFilterSource(final boolean isMatchCase) {
		this.isMatchCase = isMatchCase;
	}

	@Override
	public String getFilterSource(final String uri, final ITag tag) {
		if (value == null) {
			value = computeSource(uri, tag);
		}

		return value;
	}

	protected abstract String computeSource(String uri, ITag tag);

	@Override
	public boolean isMatchCase() {
		return isMatchCase;
	}

}
