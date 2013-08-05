package com.outgrader.proxy.core.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.core.IOutgraderProxy;
import com.outgrader.proxy.core.impl.OutgraderProxyImpl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderCoreModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IOutgraderProxy.class).to(OutgraderProxyImpl.class);
	}

}
