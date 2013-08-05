package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.StatisticsEventType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class ResponseEvent extends AbstractStatisticsEvent {

	private final String uri;

	private final int code;

	public ResponseEvent(final String uri, final int code) {
		super(StatisticsEventType.REQUEST);

		this.uri = uri;
		this.code = code;
	}

	public String getURI() {
		return uri;
	}

	public int getCode() {
		return code;
	}

}
