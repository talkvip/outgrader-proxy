package com.outgrader.proxy.statistics.module;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.statistics.export.IStatisticsExporter;
import com.outgrader.proxy.statistics.export.impl.csv.StatisticsCSVExporterImpl;
import com.outgrader.proxy.statistics.impl.StatisticsHandlerImpl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class StatisticsModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IStatisticsHandler.class).to(StatisticsHandlerImpl.class);
		bind(IStatisticsExporter.class).to(StatisticsCSVExporterImpl.class).in(Singleton.class);
	}

}
