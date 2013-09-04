package com.outgrader.proxy.statistics.manager.impl

import spock.lang.Specification

import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.statistics.events.IStatisticsEvent
import com.outgrader.proxy.statistics.events.StatisticsEventType
import com.outgrader.proxy.statistics.events.impl.AdvertismentCandidateEvent
import com.outgrader.proxy.statistics.events.impl.ErrorEvent
import com.outgrader.proxy.statistics.events.impl.RequestEvent
import com.outgrader.proxy.statistics.events.impl.ResponseEvent
import com.outgrader.proxy.statistics.manager.IStatisticsManager

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class StatisticsManagerSpec extends Specification {

	static final MESSAGE = 'error'

	static final ERROR = new UnsupportedOperationException()

	final static URI = 'uri'

	final static RULE = 'rule'

	final static int DURATION = 100

	InternalStatisticsEntry entry = Mock(InternalStatisticsEntry)

	IOutgraderProperties properties = Mock(IOutgraderProperties)

	IStatisticsManager manager

	def setup() {
		manager = new StatisticsManager(properties)
	}

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
				case StatisticsEventType.ADVERTISMENT_CANDIDATE:
					event = new AdvertismentCandidateEvent(URI, RULE)
					break
				case StatisticsEventType.ERROR:
					event = new ErrorEvent(URI, this, MESSAGE, ERROR)
					break
			}
			manager.updateStatistics(event)
		}

		then:
		2 * entry._
	}

	def "check calculated statistics"() {
		when:
		updateStatistics(URI)
		updateStatistics(URI + 2)

		and:
		def statistics = manager.exportStatistics()

		then:
		statistics.toList().size() == 2
		statistics.each { entry ->
			entry.uri == URI || entry.uri == URI + 2
			entry.requestCount == 2
			entry.responseCount == 2
			entry.maxDuration == DURATION * 2
			entry.minDuration == DURATION
			entry.averageDuration == DURATION * 1.5
		}
	}

	private void updateStatistics(String uri) {
		manager.updateStatistics(new RequestEvent(uri))
		manager.updateStatistics(new ResponseEvent(uri, DURATION))
		manager.updateStatistics(new RequestEvent(uri))
		manager.updateStatistics(new ResponseEvent(uri, DURATION * 2))
	}
}
