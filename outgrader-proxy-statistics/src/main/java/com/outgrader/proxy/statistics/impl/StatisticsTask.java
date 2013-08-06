package com.outgrader.proxy.statistics.impl;

import com.outgrader.proxy.statistics.events.IStatisticsEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
class StatisticsTask implements Runnable {

	private final IStatisticsEvent event;

	private final StatisticsManager manager;

	public StatisticsTask(final IStatisticsEvent event) {
		this(event, StatisticsManager.getInstance());
	}

	protected StatisticsTask(final IStatisticsEvent event, final StatisticsManager manager) {
		this.event = event;
		this.manager = manager;
	}

	@Override
	public void run() {
		manager.updateStatistics(event);
	}
}
