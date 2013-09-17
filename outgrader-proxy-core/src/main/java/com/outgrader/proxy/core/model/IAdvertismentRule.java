package com.outgrader.proxy.core.model;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRule {

	boolean isRuleStarted(String uri, ITag tag);

	boolean isRuleContinues(ITag ruleStartTag, ITag currentTag);

	boolean isRuleRewriteStarted(ITag startTag, ITag currentTag);

	boolean isRuleRewriteContinues(ITag ruleRewriteStartTag, ITag currentTag);

}
