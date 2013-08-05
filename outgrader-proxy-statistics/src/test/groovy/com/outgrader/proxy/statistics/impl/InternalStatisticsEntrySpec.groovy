package com.outgrader.proxy.statistics.impl

import spock.lang.Specification

import com.outgrader.proxy.statistics.impl.StatisticsManager.InternalStatisticsEntry

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class InternalStatisticsEntrySpec extends Specification {

	static final VALUES = [100, 200, 300, 400, 500]

	def "check maximum duration calculation"() {
		when:
		def InternalStatisticsEntry entry = new InternalStatisticsEntry()
		and:
		VALUES.each { value -> entry.updateResponse(value) }

		then:
		entry.maxDuration.get() == VALUES.max()
	}

	def "check minimum duration calculation"() {
		when:
		def InternalStatisticsEntry entry = new InternalStatisticsEntry()
		and:
		VALUES.each { value -> entry.updateResponse(value) }

		then:
		entry.minDuration.get() == VALUES.min()
	}

	def "check average duration calculation"() {
		when:
		def InternalStatisticsEntry entry = new InternalStatisticsEntry()
		and:
		VALUES.each { value -> entry.updateResponse(value) }

		then:
		entry.averageDuration.get() == (VALUES.sum() / VALUES.size())
	}

	def "check response count calculation"() {
		when:
		def InternalStatisticsEntry entry = new InternalStatisticsEntry()
		and:
		VALUES.each { value -> entry.updateResponse(value) }

		then:
		entry.responseCount.get() == VALUES.size()
	}

	def "check request count calculation"() {
		when:
		def InternalStatisticsEntry entry = new InternalStatisticsEntry()
		and:
		VALUES.each { value -> entry.updateRequest() }

		then:
		entry.requestCount.get() == VALUES.size()
	}
}
