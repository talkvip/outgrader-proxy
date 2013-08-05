package com.outgrader.proxy.core.statistics;


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public interface IStatisticsHandler {

	void initialize();

	void onRequestHandled(String uri);

	void onResponseHandled(String uri, long duration);

	void finish();

}
