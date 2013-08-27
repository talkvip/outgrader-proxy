package com.outgrader.proxy.statistics.export.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.statistics.exceptions.StatisticsExportException;
import com.outgrader.proxy.statistics.export.IStatisticsExporter;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;
import com.outgrader.proxy.statistics.manager.impl.StatisticsManager;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public abstract class AbstractStatisticsExporter implements IStatisticsExporter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractStatisticsExporter.class);

	@Override
	public void run() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start run()");
		}

		LOGGER.info("Starting statistics export");

		try {
			for (StatisticsEntry entry : getStatistics()) {
				exportEntry(entry);
			}
		} catch (StatisticsExportException e) {
			LOGGER.error("An error occured during statistics export", e);
		} finally {
			try {
				finish();
			} catch (StatisticsExportException e) {
				LOGGER.error("An error occured during finishing exporter", e);
			}
		}

		LOGGER.info("Finishing statistics export");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish run()");
		}

	}

	protected Iterable<StatisticsEntry> getStatistics() {
		return StatisticsManager.getInstance().exportStatistics();
	}

	protected abstract void exportEntry(StatisticsEntry entry)
			throws StatisticsExportException;

	protected abstract void finish() throws StatisticsExportException;

}
