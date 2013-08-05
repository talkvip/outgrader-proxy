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

	protected AbstractStatisticsEvent(final StatisticsEventType type) {
		this.type = type;
	}

	@Override
	public StatisticsEventType getType() {
		return type;
	}

}
