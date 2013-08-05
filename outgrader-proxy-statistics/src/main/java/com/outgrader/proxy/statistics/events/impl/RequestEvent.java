package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.StatisticsEventType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class RequestEvent extends AbstractStatisticsEvent {

	private final String uri;

	public RequestEvent(final String uri) {
		super(StatisticsEventType.REQUEST);

		this.uri = uri;
	}

	public String getURI() {
		return uri;
	}

}
