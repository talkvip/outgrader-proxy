package com.outgrader.proxy.core.advertisment.storage.impl;

import java.util.Collection;
import java.util.Collections;

import com.outgrader.proxy.core.advertisment.IAdvertismentRule;
import com.outgrader.proxy.core.advertisment.storage.IAdvertismentRuleStorage;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class AdvertismentRuleStorageImpl implements IAdvertismentRuleStorage {

	@Override
	public Collection<IAdvertismentRule> getRules() {
		return Collections.emptyList();
	}

}
