package com.outgrader.proxy.statistics.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.impl.RequestEvent;
import com.outgrader.proxy.statistics.events.impl.ResponseEvent;
import com.outgrader.proxy.statistics.export.IStatisticsExporter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Singleton
public class StatisticsHandlerImpl implements IStatisticsHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsHandlerImpl.class);

	@Inject
	IOutgraderProperties properties;

	@Inject
	IStatisticsExporter exporter;

	ExecutorService updateExecutor;

	ScheduledExecutorService exportExecutor;

	@Override
	public void onRequestHandled(final String uri) {
		handleEvent(new RequestEvent(uri));
	}

	@Override
	public void onResponseHandled(final String uri, final long duration) {
		handleEvent(new ResponseEvent(uri, duration));
	}

	private void handleEvent(final IStatisticsEvent event) {
		updateExecutor.submit(new StatisticsTask(event));
	}

	@Override
	public void initialize() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start initialize()");
		}

		LOGGER.info("Initializing Statistics module");
		initializeExecutor();
		initializeExportTask();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish initialize()");
		}
	}

	private void initializeExecutor() {
		LOGGER.info("Initializing Executor service for " + properties.getStatisticsThreadNumber() + " threads");

		updateExecutor = Executors.newFixedThreadPool(properties.getStatisticsThreadNumber());

		LOGGER.info("Executor service initialized");
	}

	private void initializeExportTask() {
		LOGGER.info("Initializing Export statistics Task");

		exportExecutor = Executors.newSingleThreadScheduledExecutor();
		exportExecutor.scheduleAtFixedRate(exporter, properties.getStatisticsExportPeriod(), properties.getStatisticsExportPeriod(),
				TimeUnit.MINUTES);

		LOGGER.info("Export statistics task initialized and scheduled");
	}

	@Override
	public void finish() {
		if (updateExecutor != null) {
			updateExecutor.shutdownNow();
		}
		if (exportExecutor != null) {
			exportExecutor.shutdownNow();
		}
	}

	@Override
	public void onAdvertismentCandidateFound(final String uri, final String ruleText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(final Object source, final String errorText, final Exception error) {
		// TODO Auto-generated method stub

	}
}
