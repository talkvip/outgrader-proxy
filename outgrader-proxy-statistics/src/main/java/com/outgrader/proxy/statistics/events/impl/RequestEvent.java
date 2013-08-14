package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.StatisticsEventType;
import com.outgrader.proxy.statistics.events.impl.internal.AbstractStatisticsEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class RequestEvent extends AbstractStatisticsEvent {

	public RequestEvent(final String uri) {
		super(StatisticsEventType.REQUEST, uri);
	}

}
