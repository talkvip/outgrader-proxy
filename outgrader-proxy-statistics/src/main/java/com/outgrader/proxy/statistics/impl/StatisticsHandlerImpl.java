package com.outgrader.proxy.statistics.impl;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.impl.RequestEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Singleton
public class StatisticsHandlerImpl implements IStatisticsHandler {

	@Inject
	IOutgraderProperties properties;

	private ExecutorService executor;

	@Override
	public void onRequestHandled(final String uri) {
		handleEvent(new RequestEvent(uri));
	}

	@Override
	public void onResponseHandled(final String uri, final HttpResponseStatus status) {
		// TODO Auto-generated method stub

	}

	protected void handleEvent(final IStatisticsEvent event) {
		executor.submit(new StatisticsTask(event));
	}

	@Override
	public void initialize() {
		executor = Executors.newFixedThreadPool(properties.getStatisticsThreadNumber());
	}

}
