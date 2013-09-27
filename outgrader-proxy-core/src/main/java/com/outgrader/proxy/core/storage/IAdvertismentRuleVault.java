package com.outgrader.proxy.core.storage;

import com.outgrader.proxy.core.model.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.11-SNAPSHOT
 * 
 */
public interface IAdvertismentRuleVault {

	IAdvertismentRule[] getRules();

	IAdvertismentRuleVault getSubVault(String key);

	boolean isEmpty();

}
