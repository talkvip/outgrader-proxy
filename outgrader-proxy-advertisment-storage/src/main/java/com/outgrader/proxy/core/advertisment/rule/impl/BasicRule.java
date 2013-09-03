package com.outgrader.proxy.core.advertisment.rule.impl;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.advertisment.rule.impl.internal.AbstractTextRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class BasicRule extends AbstractTextRule {

	public BasicRule(final String text, final IFilter filter) {
		super(text, filter);
	}

	@Override
	protected boolean matches(final String tagText) {
		return getFilter().matches(tagText);
	}

}
