package com.outgrader.proxy.core.model;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRule {

	public static final IAdvertismentRule[] EMPTY_RULES = new IAdvertismentRule[0];

	boolean matches(String uri, ITag tag);

	boolean isRuleRewriteContinues(ITag ruleRewriteStartTag, ITag currentTag);

	IAdvertismentRule[] getSubRules();

}
