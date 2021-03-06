package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.StatisticsEventType;
import com.outgrader.proxy.statistics.events.impl.internal.AbstractStatisticsEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class ResponseEvent extends AbstractStatisticsEvent {

	private final long duration;

	public ResponseEvent(final String uri, final long duration, final long timestamp) {
		super(StatisticsEventType.RESPONSE, uri, timestamp);

		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}

}
