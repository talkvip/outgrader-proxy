package com.outgrader.proxy.core.advertisment.storage.impl;

import java.util.ArrayList;
import java.util.List;
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

	private IAdvertismentRule rules[] = IAdvertismentRule.EMPTY_RULES;

	private List<IAdvertismentRule> ruleList = new ArrayList<>();

	private final Map<String, AdvertismentRuleVault> subVaults = new ConcurrentHashMap<>();

	public AdvertismentRuleVault() {
		this(new ArrayList<IAdvertismentRule>());
	}

	private AdvertismentRuleVault(final List<IAdvertismentRule> rules) {
		this.ruleList.addAll(rules);
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
			AdvertismentRuleVault newVault = new AdvertismentRuleVault(ruleList);

			subVaults.put(key, newVault);

			result = newVault;
		}

		return result;
	}

	public void addRule(final IAdvertismentRule rule) {
		ruleList.add(rule);

		for (AdvertismentRuleVault subVault : subVaults.values()) {
			subVault.addRule(rule);
		}
	}

	public void close() {
		rules = ruleList.toArray(new IAdvertismentRule[ruleList.size()]);
		ruleList.clear();
		ruleList = null;

		for (AdvertismentRuleVault subVault : subVaults.values()) {
			subVault.close();
		}
	}
}
