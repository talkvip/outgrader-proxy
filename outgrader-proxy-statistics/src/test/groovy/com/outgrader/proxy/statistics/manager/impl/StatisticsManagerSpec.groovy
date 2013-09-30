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

	static final ERROR = new UnsupportedOperationException('some message')

	final static URI = 'uri'

	final static RULE = 'rule'

	final static int DURATION = 100

	InternalStatisticsEntry entry = Mock(InternalStatisticsEntry)

	IOutgraderProperties properties = Mock(IOutgraderProperties)

	IStatisticsManager manager

	def setup() {
		properties.statisticsExportPeriod >> 1

		manager = new StatisticsManager(properties)
	}

	def cleanup() {
		manager.statistics.clear()
	}

	def addKey(def uri, def entry) {
		RequestEvent event = new RequestEvent(uri)
		manager.statistics.put(manager.getKey(event), entry)
	}

	def "check statistics entry updated on RequestEvent"() {
		when:
		addKey(URI, entry)
		and:
		manager.updateStatistics(new RequestEvent(URI))

		then:
		1 * entry.updateRequest()
	}

	def "check statistics entry updated on ResponseEvent"() {
		when:
		addKey(URI, entry)
		and:
		manager.updateStatistics(new ResponseEvent(URI, DURATION))

		then:
		1 * entry.updateResponse(DURATION)
	}

	def "check statistics entry updated on ErrorEvent"() {
		when:
		addKey(URI, entry)
		and:
		manager.updateStatistics(new ErrorEvent(URI, this, MESSAGE, ERROR))

		then:
		1 * entry.updateError()
	}

	def "check statistics entry updated on AdvertismentCandidateEvent"() {
		when:
		addKey(URI, entry)
		and:
		manager.updateStatistics(new AdvertismentCandidateEvent(URI, MESSAGE))

		then:
		1 * entry.updateAdvertismentCandidateCount()
	}

	def "check new statistics entry added if uri not exists"() {
		when:
		manager.updateStatistics(new ResponseEvent(URI, DURATION))

		then:
		manager.statistics.size() == 1
	}

	def "check manager supports statistics of any type"() {
		when:
		addKey(URI, entry)
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
		4 * entry._
	}

	def "check calculated statistics"() {
		when:
		updateStatistics(URI)
		updateStatistics(URI + 2)

		and:
		def statistics = manager.exportStatistics(true)

		then:
		statistics.toList().size() == 2
		statistics.each { entry -> assertEntry(entry) }
	}

	def assertEntry(def entry) {
		assert entry.uri == URI || entry.uri == URI + 2
		assert entry.requestCount == 2
		assert entry.responseCount == 2
		assert entry.maxDuration == DURATION * 2
		assert entry.minDuration == DURATION
		assert entry.averageDuration == DURATION * 1.5
		assert entry.errorCount == 1
		assert entry.advertismentCandidateCount == 1
	}

	private void updateStatistics(String uri) {
		manager.updateStatistics(new RequestEvent(uri))
		manager.updateStatistics(new ResponseEvent(uri, DURATION))
		manager.updateStatistics(new RequestEvent(uri))
		manager.updateStatistics(new ResponseEvent(uri, DURATION * 2))
		manager.updateStatistics(new ErrorEvent(uri, this, MESSAGE, ERROR))
		manager.updateStatistics(new AdvertismentCandidateEvent(uri, MESSAGE))
	}
}
