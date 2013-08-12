package com.outgrader.proxy.core.advertisment.storage;

import java.util.Collection;

import com.outgrader.proxy.core.advertisment.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRuleStorage {

	Collection<IAdvertismentRule> getRules();

}
