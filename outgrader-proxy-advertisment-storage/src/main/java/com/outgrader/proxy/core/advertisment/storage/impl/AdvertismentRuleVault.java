package com.outgrader.proxy.core.advertisment.storage.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.storage.IAdvertismentRuleVault;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.11-SNAPSHOT
 * 
 */
class AdvertismentRuleVault implements IAdvertismentRuleVault {

	private static final List<IAdvertismentRule> EMPTY_RULE_LIST = Collections.emptyList();

	private IAdvertismentRule rules[] = IAdvertismentRule.EMPTY_RULES;

	private List<IAdvertismentRule> ruleList = new ArrayList<>();

	private Map<String, AdvertismentRuleVault> subVaults;

	public AdvertismentRuleVault() {
		this(EMPTY_RULE_LIST);
	}

	private AdvertismentRuleVault(final List<IAdvertismentRule> rules) {
		this.ruleList.addAll(rules);
	}

	@Override
	public IAdvertismentRule[] getRules() {
		return rules;
	}

	@Override
	public IAdvertismentRuleVault getSubVault(final String key) {
		if (subVaults == null) {
			return null;
		}
		return subVaults.get(key);
	}

	public AdvertismentRuleVault createSubVault(final String key) {
		AdvertismentRuleVault result = (AdvertismentRuleVault) getSubVault(key);

		if (result == null) {
			AdvertismentRuleVault newVault = new AdvertismentRuleVault(ruleList);

			if (subVaults == null) {
				subVaults = new HashMap<>();
			}
			subVaults.put(key, newVault);

			result = newVault;
		}

		return result;
	}

	public void addRule(final IAdvertismentRule rule) {
		ruleList.add(rule);

		if (subVaults != null) {
			for (AdvertismentRuleVault subVault : subVaults.values()) {
				subVault.addRule(rule);
			}
		}
	}

	public void close() {
		rules = ruleList.toArray(new IAdvertismentRule[ruleList.size()]);
		ruleList.clear();
		ruleList = null;

		if (subVaults != null) {
			for (AdvertismentRuleVault subVault : subVaults.values()) {
				subVault.close();
			}
		}
	}
}
