package com.outgrader.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.outgrader.proxy.core.IOutgraderProxy;
import com.outgrader.proxy.core.module.OutgraderCoreModule;
import com.outgrader.proxy.external.module.ExternalSenderModule;
import com.outgrader.proxy.properties.module.OutgraderPropertiesModule;
import com.outgrader.proxy.statistics.module.StatisticsModule;

/**
 * Main entry point to application
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0
 */
public class Outgrader {

	private static final Logger LOGGER = LoggerFactory.getLogger(Outgrader.class);

	public static void main(final String[] args) {
		LOGGER.info("Starting Ougrader-Runner application");

		LOGGER.info("Initializing Guice environment");
		Injector injector = Guice.createInjector(new OutgraderCoreModule(), new OutgraderPropertiesModule(), new ExternalSenderModule(),
				new StatisticsModule());

		LOGGER.info("Creating instance of Outgrader Proxy and start it");
		IOutgraderProxy proxy = injector.getInstance(IOutgraderProxy.class);
		proxy.start();

		LOGGER.info("Closing Outgrader-Runner application");
	}

}
