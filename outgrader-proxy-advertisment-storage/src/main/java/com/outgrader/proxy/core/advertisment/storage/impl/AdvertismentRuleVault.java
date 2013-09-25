package com.outgrader.proxy.core.advertisment.storage.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.storage.IAdvertismentRuleVault;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.11-SNAPSHOT
 * 
 */
class AdvertismentRuleVault implements IAdvertismentRuleVault {

	private final IAdvertismentRule[] rules = IAdvertismentRule.EMPTY_SUB_RULES;

	private final Map<String, IAdvertismentRuleVault> subVaults = new ConcurrentHashMap<>();

	@Override
	public IAdvertismentRule[] getIncludingRules() {
		return rules;
	}

	@Override
	public IAdvertismentRuleVault getSubVault(final String key) {
		return subVaults.get(key);
	}

}
