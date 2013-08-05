package com.outgrader.proxy.core.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.core.IOutgraderProxy;
import com.outgrader.proxy.core.handler.IOutgraderFrontendHandler;
import com.outgrader.proxy.core.handler.impl.OutgraderFrontendHandler;
import com.outgrader.proxy.core.impl.OutgraderProxyImpl;
import com.outgrader.proxy.core.initializer.IOutgraderChannelInitializer;
import com.outgrader.proxy.core.initializer.impl.OutgraderChannelInitializer;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderCoreModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IOutgraderProxy.class).to(OutgraderProxyImpl.class);
		bind(IOutgraderFrontendHandler.class).to(OutgraderFrontendHandler.class);
		bind(IOutgraderChannelInitializer.class).to(OutgraderChannelInitializer.class);
	}
}
