package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.StatisticsEventType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class ResponseEvent extends AbstractStatisticsEvent {

	private final long duration;

	public ResponseEvent(final String uri, final long duration) {
		super(StatisticsEventType.RESPONSE, uri);

		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}

}
