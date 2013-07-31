package com.outgrader.proxy.properties.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.properties.impl.OutgraderPropertiesImpl;
import com.outgrager.proxy.core.properties.IOutgraderProperties;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderPropertiesModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IOutgraderProperties.class).to(OutgraderPropertiesImpl.class);
	}

}
