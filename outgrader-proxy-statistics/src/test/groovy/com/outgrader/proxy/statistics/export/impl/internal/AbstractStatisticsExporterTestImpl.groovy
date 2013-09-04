package com.outgrader.proxy.statistics.export.impl.internal

import com.outgrader.proxy.statistics.export.impl.AbstractStatisticsExporter
import com.outgrader.proxy.statistics.impl.StatisticsEntry
import com.outgrader.proxy.statistics.manager.IStatisticsManager

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class AbstractStatisticsExporterTestImpl extends AbstractStatisticsExporter {

	public AbstractStatisticsExporterTestImpl(IStatisticsManager manager) {
		super(manager)
	}

	@Override
	protected void exportEntry(StatisticsEntry entry) {
		// do nothing

	}

	@Override
	protected void finish() {
		// do nothing

	}
}
