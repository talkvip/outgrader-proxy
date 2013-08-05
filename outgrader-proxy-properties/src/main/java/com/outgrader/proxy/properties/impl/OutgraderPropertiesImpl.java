package com.outgrader.proxy.properties.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.properties.source.IPropertiesSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderPropertiesImpl implements IOutgraderProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderPropertiesImpl.class);

	private static final String PROXY_PORT = "outgrader.proxy.port";

	private static final String BOSS_THREADS = "outgrader.proxy.boss_threads";

	private static final String WORKER_THREADS = "outgrader.proxy.worker_threads";

	private static final String STATISTICS_THREADS = "outgrader.proxy.statistics_threads";

	private Configuration configuration;

	@Inject
	private IPropertiesSource propertiesSource;

	@Override
	public int getPort() {
		return getConfiguration().getInt(PROXY_PORT);
	}

	private Configuration getConfiguration() {
		if (configuration == null) {
			initialize();
		}

		return configuration;
	}

	protected void initialize() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start initialize()");
		}

		LOGGER.info("Initializing Outgrader properties");
		configuration = propertiesSource.getConfiguration();

		checkNotNull(configuration, "Configuration cannot be null");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish initialize()");
		}
	}

	@Override
	public int getBossThreadNumber() {
		return getConfiguration().getInt(BOSS_THREADS);
	}

	@Override
	public int getWorkerThreadNumber() {
		return getConfiguration().getInt(WORKER_THREADS);
	}

	@Override
	public int getStatisticsThreadNumber() {
		return getConfiguration().getInt(STATISTICS_THREADS);
	}
}
