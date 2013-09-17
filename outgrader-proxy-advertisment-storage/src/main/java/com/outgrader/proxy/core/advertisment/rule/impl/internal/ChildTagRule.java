package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import org.apache.commons.lang3.ObjectUtils;

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
		return startTag.equals(currentTag);
	}

	@Override
	public boolean isRuleContinues(final ITag ruleStartTag, final ITag currentTag) {
		return ruleStartTag.equals(currentTag);
	}

	@Override
	public boolean isRuleRewriteContinues(final ITag ruleRewriteStartTag, final ITag currentTag) {
		return !ObjectUtils.equals(currentTag.getOpeningTag(), ruleRewriteStartTag);
	}

}
