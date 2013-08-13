package com.outgrader.proxy.core.advertisment.storage.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.advertisment.storage.IAdvertismentRuleStorage;
import com.outgrader.proxy.core.advertisment.storage.impl.AdvertismentRuleStorageImpl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class AdvertismentRuleStorageModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAdvertismentRuleStorage.class).to(AdvertismentRuleStorageImpl.class);
	}

}
