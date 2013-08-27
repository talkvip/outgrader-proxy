package com.outgrader.proxy.advertisment.storage;

import com.outgrader.proxy.advertisment.rule.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRuleStorage {

	IAdvertismentRule[] getRules();

}
