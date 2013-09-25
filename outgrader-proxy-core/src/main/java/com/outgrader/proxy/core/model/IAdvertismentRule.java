package com.outgrader.proxy.core.model;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRule {

	boolean matches(String uri, ITag tag);

	boolean isRuleRewriteContinues(ITag ruleRewriteStartTag, ITag currentTag);

	IAdvertismentRule[] getSubRules();

}
