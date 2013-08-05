package com.outgrader.proxy.statistics;

import io.netty.handler.codec.http.HttpResponseStatus;

import javax.inject.Singleton;

import com.outgrader.proxy.core.statistics.IStatisticsHandler;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Singleton
public class StatisticsHandlerImpl implements IStatisticsHandler {

	@Override
	public void onRequestHandled(final String uri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponseHandled(final String uri, final HttpResponseStatus status) {
		// TODO Auto-generated method stub

	}

}
