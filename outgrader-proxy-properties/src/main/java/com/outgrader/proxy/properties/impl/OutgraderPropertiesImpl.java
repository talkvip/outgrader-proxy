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
}
