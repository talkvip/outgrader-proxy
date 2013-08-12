package com.outgrader.proxy.core.advertisment.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.core.advertisment.response.IAdvertismentProcessor;
import com.outgrader.proxy.core.advertisment.response.impl.AdvertismentProcessorImpl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class AdvertismentProcessorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAdvertismentProcessor.class).to(AdvertismentProcessorImpl.class);
	}

}
