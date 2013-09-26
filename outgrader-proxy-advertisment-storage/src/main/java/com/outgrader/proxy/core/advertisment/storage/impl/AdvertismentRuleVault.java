package com.outgrader.proxy.core.advertisment.storage.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;

import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.storage.IAdvertismentRuleVault;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.11-SNAPSHOT
 * 
 */
class AdvertismentRuleVault implements IAdvertismentRuleVault {

	private IAdvertismentRule[] rules = IAdvertismentRule.EMPTY_SUB_RULES;

	private final Map<String, AdvertismentRuleVault> subVaults = new ConcurrentHashMap<>();

	public AdvertismentRuleVault() {
		this(IAdvertismentRule.EMPTY_SUB_RULES);
	}

	private AdvertismentRuleVault(final IAdvertismentRule[] rules) {
		this.rules = rules;
	}

	@Override
	public IAdvertismentRule[] getIncludingRules() {
		return rules;
	}

	@Override
	public IAdvertismentRuleVault getSubVault(final String key) {
		return subVaults.get(key);
	}

	public AdvertismentRuleVault createSubVault(final String key) {
		AdvertismentRuleVault result = (AdvertismentRuleVault) getSubVault(key);

		if (result == null) {
			AdvertismentRuleVault newVault = new AdvertismentRuleVault(getIncludingRules());

			subVaults.put(key, newVault);

			result = newVault;
		}

		return result;
	}

	public void addRule(final IAdvertismentRule rule) {
		rules = ArrayUtils.add(rules, rule);

		for (AdvertismentRuleVault subVault : subVaults.values()) {
			subVault.addRule(rule);
		}
	}
}
