package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import org.apache.commons.lang3.ArrayUtils;
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
public class AdvertismentRuleImpl implements IAdvertismentRule {

	private static final String EMBED_TAG = "embed";

	private final String text;

	private final IFilter filter;

	private IAdvertismentRule[] subRules = EMPTY_RULES;

	private SubRuleType subRuleType = SubRuleType.NONE;

	public AdvertismentRuleImpl(final String text, final IFilter filter) {
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
	public boolean matches(final String uri, final ITag tag) {
		return getFilter().matches(uri, tag);
	}

	@Override
	public boolean isRuleRewriteContinues(final ITag ruleRewriteStartTag,
			final ITag currentTag) {
		return !ObjectUtils.equals(currentTag.getOpeningTag(),
				ruleRewriteStartTag)
				&& !((ruleRewriteStartTag.getTagType() == TagType.OPENING) && ruleRewriteStartTag
						.getName().equals(EMBED_TAG));
	}

	public void addSubRule(final IAdvertismentRule subRule) {
		subRules = ArrayUtils.add(subRules, subRule);
	}

	@Override
	public IAdvertismentRule[] getSubRules() {
		return subRules;
	}

	@Override
	public SubRuleType getSubRuleType() {
		return subRuleType;
	}

	public void setSubRuleType(final SubRuleType subRuleType) {
		this.subRuleType = subRuleType;
	}
}
