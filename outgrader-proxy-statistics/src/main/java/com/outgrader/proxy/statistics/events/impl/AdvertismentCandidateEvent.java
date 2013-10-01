package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.StatisticsEventType;
import com.outgrader.proxy.statistics.events.impl.internal.AbstractStatisticsEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public class AdvertismentCandidateEvent extends AbstractStatisticsEvent {

	private final String rule;

	public AdvertismentCandidateEvent(final String uri, final String rule, final long timestamp) {
		super(StatisticsEventType.ADVERTISMENT_CANDIDATE, uri, timestamp);

		this.rule = rule;
	}

	public String getRule() {
		return rule;
	}

}
