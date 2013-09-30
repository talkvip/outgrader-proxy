package com.outgrader.proxy.statistics.export.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.outgrader.proxy.statistics.exceptions.StatisticsExportException;
import com.outgrader.proxy.statistics.export.IStatisticsExporter;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;
import com.outgrader.proxy.statistics.manager.IStatisticsManager;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public abstract class AbstractStatisticsExporter implements IStatisticsExporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatisticsExporter.class);

	private final IStatisticsManager manager;

	protected AbstractStatisticsExporter(final IStatisticsManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start run()");
		}

		try {
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
		} catch (Throwable e) {
			LOGGER.error("An error occured during Statistics Export", e);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish run()");
		}

	}

	protected Iterable<StatisticsEntry> getStatistics() {
		List<StatisticsEntry> result = Lists.newArrayList(manager.exportStatistics(false));

		Collections.sort(result);

		return result;
	}

	protected abstract void exportEntry(StatisticsEntry entry) throws StatisticsExportException;

	protected abstract void finish() throws StatisticsExportException;

}
