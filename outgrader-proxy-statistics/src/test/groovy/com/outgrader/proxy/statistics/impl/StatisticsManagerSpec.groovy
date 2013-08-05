package com.outgrader.proxy.statistics.impl

import spock.lang.Specification

import com.outgrader.proxy.statistics.events.impl.RequestEvent
import com.outgrader.proxy.statistics.events.impl.ResponseEvent
import com.outgrader.proxy.statistics.impl.StatisticsManager.InternalStatisticsEntry

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class StatisticsManagerSpec extends Specification {

	final static URI = 'uri'

	final int DURATION = 100

	InternalStatisticsEntry entry = Mock(InternalStatisticsEntry)

	StatisticsManager manager = new StatisticsManager()

	def "check statistics entry updated on RequestEvent"() {
		when:
		manager.statistics.put(URI, entry)
		and:
		manager.updateStatistics(new RequestEvent(URI))

		then:
		entry.updateRequest()
	}

	def "check statistics entry updated on ResponseEvent"() {
		when:
		manager.statistics.put(URI, entry)
		and:
		manager.updateStatistics(new ResponseEvent(URI, DURATION))

		then:
		entry.updateResponse(DURATION)
	}

	def "check new statistics entry added if uri not exists"() {
		when:
		manager.updateStatistics(new ResponseEvent(URI, DURATION))

		then:
		manager.statistics.size() == 1
		manager.statistics.containsKey(URI)
	}
}
