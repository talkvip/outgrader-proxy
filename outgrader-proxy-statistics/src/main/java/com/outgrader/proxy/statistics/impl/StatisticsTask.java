package com.outgrader.proxy.statistics.impl;

import com.outgrader.proxy.statistics.events.IStatisticsEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
class StatisticsTask implements Runnable {

	private final IStatisticsEvent event;

	public StatisticsTask(final IStatisticsEvent event) {
		this.event = event;
	}

	@Override
	public void run() {
		StatisticsManager.getInstance().updateStatistics(event);
	}

}
