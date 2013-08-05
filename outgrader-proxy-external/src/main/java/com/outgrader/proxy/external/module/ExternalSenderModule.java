package com.outgrader.proxy.external.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.core.external.IExternalSender;
import com.outgrader.proxy.external.impl.ExternalSenderImpl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class ExternalSenderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IExternalSender.class).to(ExternalSenderImpl.class);
	}
}
