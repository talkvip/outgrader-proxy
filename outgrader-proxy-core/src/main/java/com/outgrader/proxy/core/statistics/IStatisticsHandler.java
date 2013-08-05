package com.outgrader.proxy.core.statistics;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public interface IStatisticsHandler {

	void onRequestHandled(String uri);

	void onResponseHandled(String uri, HttpResponseStatus status);

}
