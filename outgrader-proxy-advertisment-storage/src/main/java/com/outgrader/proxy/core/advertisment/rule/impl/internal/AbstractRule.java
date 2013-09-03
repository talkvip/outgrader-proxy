package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.model.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractRule implements IAdvertismentRule {

	private final String text;

	private final IFilter filter;

	protected AbstractRule(final String text, final IFilter filter) {
		this.text = text;
		this.filter = filter;
	}

	protected IFilter getFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return text;
	}

}
