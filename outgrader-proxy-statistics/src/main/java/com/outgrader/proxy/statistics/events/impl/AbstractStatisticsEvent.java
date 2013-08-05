package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.StatisticsEventType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public abstract class AbstractStatisticsEvent implements IStatisticsEvent {

	private final StatisticsEventType type;

	private final String uri;

	protected AbstractStatisticsEvent(final StatisticsEventType type, final String uri) {
		this.type = type;
		this.uri = uri;
	}

	@Override
	public StatisticsEventType getType() {
		return type;
	}

	@Override
	public String getURI() {
		return uri;
	}

}
