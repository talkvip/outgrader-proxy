package com.outgrader.proxy.statistics.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.impl.AdvertismentCandidateEvent;
import com.outgrader.proxy.statistics.events.impl.ErrorEvent;
import com.outgrader.proxy.statistics.events.impl.RequestEvent;
import com.outgrader.proxy.statistics.events.impl.ResponseEvent;
import com.outgrader.proxy.statistics.export.IStatisticsExporter;
import com.outgrader.proxy.statistics.manager.IStatisticsManager;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Component
public class StatisticsHandlerImpl implements IStatisticsHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsHandlerImpl.class);

	@Inject
	private IOutgraderProperties properties;

	@Inject
	private IStatisticsExporter exporter;

	@Inject
	private IStatisticsManager manager;

	private ExecutorService updateExecutor;

	private ScheduledExecutorService exportExecutor;

	private final ThreadLocal<Long> currentTimestamps = new ThreadLocal<>();

	@Override
	public void onRequestHandled(final String uri) {
		currentTimestamps.set(System.currentTimeMillis());

		handleEvent(new RequestEvent(uri, currentTimestamps.get()));
	}

	@Override
	public void onResponseHandled(final String uri, final long duration) {
		handleEvent(new ResponseEvent(uri, duration, currentTimestamps.get()));
		currentTimestamps.remove();
	}

	private void handleEvent(final IStatisticsEvent event) {
		updateExecutor.submit(new Runnable() {

			@Override
			public void run() {
				manager.updateStatistics(event);
			}
		});
	}

	@PostConstruct
	protected void initialize() {
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
		exportExecutor.scheduleWithFixedDelay(exporter, properties.getStatisticsExportPeriod(), properties.getStatisticsExportPeriod(),
				TimeUnit.MINUTES);

		LOGGER.info("Export statistics task initialized and scheduled");
	}

	@PreDestroy
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
		handleEvent(new AdvertismentCandidateEvent(uri, ruleText, currentTimestamps.get()));
	}

	@Override
	public void onError(final String uri, final Object source, final String errorText, final Throwable error) {
		handleEvent(new ErrorEvent(uri, source, errorText, error, currentTimestamps.get()));

	}
}
