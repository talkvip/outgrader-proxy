package com.outgrader.proxy.properties.source.file;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.properties.impl.OutgraderPropertiesImpl;
import com.outgrader.proxy.properties.source.IPropertiesSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Component
public class FilePropertiesSource implements IPropertiesSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilePropertiesSource.class);

	private static final String[] PROPERTIES_LOCATIONS = { "/outgrader.properties", "/test-outgrader.properties",
			"/default-outgrader.properties" };

	@Override
	public Configuration getConfiguration() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start getConfiguration()");
		}

		Configuration result = null;

		try {
			URL location = getPropertiesLocation();

			checkNotNull(location, "Properties file location cannot be null");

			result = new PropertiesConfiguration(location);
		} catch (ConfigurationException e) {
			LOGGER.error("An exception occured during processing Properties file", e);

			throw new RuntimeException(e);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish getConfiguration() -> " + result);
		}

		return result;
	}

	protected URL getPropertiesLocation() {
		URL result = null;

		LOGGER.info("Determining config file location");

		for (String locationCandidate : getLocationCandidates()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Check properties file location at <" + locationCandidate + ">");
			}

			result = toURL(locationCandidate);

			if (result != null) {
				break;
			}
		}

		if (result != null) {
			LOGGER.info("Properties file found at <" + result.getFile() + ">");
		} else {
			LOGGER.error("Properties file not found");
		}

		return result;
	}

	protected String[] getLocationCandidates() {
		return PROPERTIES_LOCATIONS;
	}

	protected URL toURL(final String location) {
		return OutgraderPropertiesImpl.class.getResource(location);
	}
}
