package com.outgrader.proxy.properties.module;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.properties.impl.OutgraderPropertiesImpl;
import com.outgrader.proxy.properties.source.IPropertiesSource;
import com.outgrader.proxy.properties.source.file.FilePropertiesSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderPropertiesModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IOutgraderProperties.class).to(OutgraderPropertiesImpl.class).in(Singleton.class);
		bind(IPropertiesSource.class).to(FilePropertiesSource.class).in(Singleton.class);
	}

}
