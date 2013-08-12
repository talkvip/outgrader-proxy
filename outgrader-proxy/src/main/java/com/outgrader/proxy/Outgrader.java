package com.outgrader.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.outgrader.proxy.core.IOutgraderProxy;
import com.outgrader.proxy.core.advertisment.module.AdvertismentProcessorModule;
import com.outgrader.proxy.core.module.OutgraderCoreModule;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.external.module.ExternalSenderModule;
import com.outgrader.proxy.properties.module.OutgraderPropertiesModule;
import com.outgrader.proxy.statistics.module.StatisticsModule;

/**
 * Main entry point to application
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0
 */
public final class Outgrader {

	private static final Logger LOGGER = LoggerFactory.getLogger(Outgrader.class);

	private Injector injector;

	private Outgrader() {

	}

	/**
	 * For tests only
	 */
	protected Outgrader(final Injector injector) {
		this.injector = injector;
	}

	public void run() {
		LOGGER.info("Initializing Statistics module");
		IStatisticsHandler statisticsModule = getInjector().getInstance(IStatisticsHandler.class);
		statisticsModule.initialize();

		LOGGER.info("Creating instance of Outgrader Proxy and start it");
		IOutgraderProxy proxy = getInjector().getInstance(IOutgraderProxy.class);
		proxy.run();

		LOGGER.info("Finalize statistics module");
		statisticsModule.finish();
	}

	protected Injector getInjector() {
		LOGGER.info("Initializing Guice environment");

		if (injector == null) {
			injector = Guice.createInjector(new OutgraderCoreModule(), new OutgraderPropertiesModule(), new ExternalSenderModule(),
					new StatisticsModule(), new AdvertismentProcessorModule());
		}

		return injector;
	}

	public static void main(final String[] args) {
		LOGGER.info("Starting Ougrader-Runner application");

		new Outgrader().run();

		LOGGER.info("Closing Outgrader-Runner application");
	}
}
