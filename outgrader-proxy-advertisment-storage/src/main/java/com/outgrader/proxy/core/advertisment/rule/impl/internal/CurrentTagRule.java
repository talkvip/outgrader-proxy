package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import org.apache.commons.lang3.ObjectUtils;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.model.ITag;
import com.outgrader.proxy.core.model.ITag.TagType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class CurrentTagRule implements IAdvertismentRule {

	private static final String EMBED_TAG = "embed";

	private final String text;

	private final IFilter filter;

	public CurrentTagRule(final String text, final IFilter filter) {
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

	@Override
	public boolean isRuleStarted(final String uri, final ITag tag) {
		return getFilter().matches(uri, tag);
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
		return !ObjectUtils.equals(currentTag.getOpeningTag(), ruleRewriteStartTag)
				|| ((ruleRewriteStartTag.getTagType() == TagType.OPENING) && ruleRewriteStartTag.getName().equals(EMBED_TAG));
	}
}
