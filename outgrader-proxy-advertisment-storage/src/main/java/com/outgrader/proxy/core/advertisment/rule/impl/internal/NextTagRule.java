package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.core.advertisment.filter.IFilter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.8-SNAPSHOT
 * 
 */
public class NextTagRule extends CurrentTagRule {

	public NextTagRule(final String text, final IFilter filter) {
		super(text, filter);
	}

}
