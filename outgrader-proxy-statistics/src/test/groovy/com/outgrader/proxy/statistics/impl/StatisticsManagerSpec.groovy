package com.outgrader.proxy.statistics.impl

import spock.lang.Specification

import com.outgrader.proxy.statistics.events.IStatisticsEvent
import com.outgrader.proxy.statistics.events.StatisticsEventType
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

	StatisticsManager manager = StatisticsManager.getInstance()

	def cleanup() {
		manager.statistics.clear()
	}

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

	def "check manager supports statistics of any type"() {
		when:
		manager.statistics.put(URI, entry)
		and:
		StatisticsEventType.each { type ->
			IStatisticsEvent event = null

			switch (type){
				case StatisticsEventType.REQUEST:
					event = new RequestEvent(URI)
					break
				case StatisticsEventType.RESPONSE:
					event = new ResponseEvent(URI, DURATION)
					break
			}
			manager.updateStatistics(event)
		}

		then:
		2 * entry._
	}
}
