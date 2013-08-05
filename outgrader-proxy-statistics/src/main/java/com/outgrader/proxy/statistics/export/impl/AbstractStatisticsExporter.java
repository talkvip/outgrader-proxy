package com.outgrader.proxy.statistics.export.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.statistics.export.IStatisticsExporter;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;
import com.outgrader.proxy.statistics.impl.StatisticsManager;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public abstract class AbstractStatisticsExporter implements IStatisticsExporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatisticsExporter.class);

	@Override
	public void run() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start run()");
		}

		LOGGER.info("Starting statistics export");

		for (StatisticsEntry entry : StatisticsManager.getInstance().exportStatistics()) {
			exportEntry(entry);
		}

		finish();

		LOGGER.info("Finishing statistics export");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish run()");
		}

	}

	protected abstract void exportEntry(StatisticsEntry entry);

	protected abstract void finish();

}
