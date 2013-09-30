package com.outgrader.proxy.statistics.manager;

import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public interface IStatisticsManager {

	void updateStatistics(IStatisticsEvent event);

	Iterable<StatisticsEntry> exportStatistics(boolean full);

}
