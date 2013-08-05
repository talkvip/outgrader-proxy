package com.outgrader.proxy.statistics.impl;

import com.google.common.util.concurrent.AtomicLongMap;
import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.impl.RequestEvent;
import com.outgrader.proxy.statistics.events.impl.ResponseEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class StatisticsManager {

	private static final class StatisticsManagerHandler {
		private static volatile StatisticsManager instance = new StatisticsManager();
	}

	private final AtomicLongMap<String> requestStatistics = AtomicLongMap.create();

	private final AtomicLongMap<String> responseStatistics = AtomicLongMap.create();

	private StatisticsManager() {

	}

	public static StatisticsManager getInstance() {
		return StatisticsManagerHandler.instance;
	}

	public void updateStatistics(final IStatisticsEvent event) {
		switch (event.getType()) {
		case REQUEST:
			requestStatistics.incrementAndGet(((RequestEvent) event).getURI());
			break;
		case RESPONSE:
			responseStatistics.incrementAndGet(((ResponseEvent) event).getURI());
			break;
		}
	}

}
