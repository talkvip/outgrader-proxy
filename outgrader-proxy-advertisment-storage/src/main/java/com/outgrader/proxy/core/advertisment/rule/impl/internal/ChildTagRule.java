package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.8-SNAPSHOT
 * 
 */
public class ChildTagRule extends CurrentTagRule {

	public ChildTagRule(final String text, final IFilter filter) {
		super(text, filter);
	}

	@Override
	public boolean isRuleRewriteStarted(final ITag startTag, final ITag currentTag) {
		return true;
	}

}
