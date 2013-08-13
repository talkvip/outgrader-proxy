package com.outgrader.proxy.advertisment.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter;
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentProcessorImpl;
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentRewriterImpl;
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class AdvertismentProcessorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAdvertismentProcessor.class).to(AdvertismentProcessorImpl.class);
		bind(IAdvertismentRewriter.class).to(AdvertismentRewriterImpl.class);
	}

}
