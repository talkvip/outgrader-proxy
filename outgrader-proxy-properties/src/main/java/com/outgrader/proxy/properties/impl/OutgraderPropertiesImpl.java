package com.outgrader.proxy.properties.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.properties.source.IPropertiesSource;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Component
public class OutgraderPropertiesImpl implements IOutgraderProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderPropertiesImpl.class);

	private static final String PROXY_PORT = "outgrader.proxy.port";

	private static final String BOSS_THREADS = "outgrader.proxy.boss_threads";

	private static final String WORKER_THREADS = "outgrader.proxy.worker_threads";

	private static final String STATISTICS_THREADS = "outgrader.proxy.statistics_threads";

	private static final String STATISTICS_EXPORT_PERIOD = "outgrader.proxy.statistics.export_period";

	private static final String STATISTICS_EXPORT_DIRECTORY = "outgrader.proxy.statistics.export_directory";

	private static final String ADVERTISMENT_LIST_FILE = "outgrader.proxy.advertisments.file";

	private static final String ADVERITSMENT_TAG_CANDIDATES = "outgrader.proxy.advertisment.tags";

	private static final String PROXY_REWRITE_MODE = "outgrader.proxy.rewrite_mode";

	private Configuration configuration;

	private final IPropertiesSource propertiesSource;

	private Set<String> supportedTags = null;

	@Inject
	public OutgraderPropertiesImpl(final IPropertiesSource propertiesSource) {
		this.propertiesSource = propertiesSource;
	}

	protected Configuration getConfiguration() {
		return configuration;
	}

	@PostConstruct
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
	public int getPort() {
		return getConfiguration().getInt(PROXY_PORT);
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

	@Override
	public int getStatisticsExportPeriod() {
		return getConfiguration().getInt(STATISTICS_EXPORT_PERIOD);
	}

	@Override
	public String getStatisticsExportDirectory() {
		return getConfiguration().getString(STATISTICS_EXPORT_DIRECTORY);
	}

	@Override
	public String[] getAdvertismentListLocations() {
		return getConfiguration().getStringArray(ADVERTISMENT_LIST_FILE);
	}

	@Override
	public Set<String> getSupportedTags() {
		if (supportedTags == null) {
			String[] sources = getConfiguration().getStringArray(ADVERITSMENT_TAG_CANDIDATES);

			if (sources != null) {
				supportedTags = Sets.newHashSet(sources);
			} else {
				supportedTags = Sets.newHashSet();
			}
		}

		return supportedTags;
	}

	@Override
	public RewriteMode getRewriteMode() {
		String mode = getConfiguration().getString(PROXY_REWRITE_MODE);

		if (StringUtils.isEmpty(mode)) {
			throw new IllegalArgumentException("<" + PROXY_REWRITE_MODE + "> parameter not found in config file");
		}

		RewriteMode result = RewriteMode.valueOf(mode);

		if (result == null) {
			throw new IllegalArgumentException(PROXY_REWRITE_MODE + " should be of values <" + StringUtils.join(RewriteMode.values(), ",")
					+ "> but was <" + mode + ">");
		}
		return null;
	}
}
