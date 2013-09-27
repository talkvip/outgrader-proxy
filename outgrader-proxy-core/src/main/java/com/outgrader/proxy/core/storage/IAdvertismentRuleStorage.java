package com.outgrader.proxy.core.storage;

import com.outgrader.proxy.core.model.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRuleStorage {

	@Deprecated
	IAdvertismentRule[] getIncludingRules();

	IAdvertismentRule[] getExcludingRules();

	IAdvertismentRuleVault getIncludingRulesVault();

}
