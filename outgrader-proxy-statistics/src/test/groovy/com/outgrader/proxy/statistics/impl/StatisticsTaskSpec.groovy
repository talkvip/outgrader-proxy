package com.outgrader.proxy.statistics.impl

import spock.lang.Specification

import com.outgrader.proxy.statistics.events.IStatisticsEvent
import com.outgrader.proxy.statistics.manager.impl.StatisticsManager;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class StatisticsTaskSpec extends Specification {

	StatisticsManager manager = Mock(StatisticsManager)

	IStatisticsEvent event = Mock(IStatisticsEvent)

	StatisticsTask task

	def setup() {
		task = new StatisticsTask(event, manager)
	}

	def "manager called on statistics task run"() {
		when:
		task.run()

		then:
		manager.updateStatistics(event)
	}
}
