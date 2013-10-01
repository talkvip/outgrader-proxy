package com.outgrader.proxy.statistics.impl

import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadPoolExecutor

import spock.lang.Specification

import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.statistics.export.IStatisticsExporter

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class StatisticsHandlerImplSpec extends Specification {

	static final RULE_TEXT = 'rule text'

	static final UPDATE_THREAD_COUNT = 5

	static final EXPORT_PERIOD = 3

	static final String URI = 'some uri'

	static final int DURATION = 100

	static final MESSAGE = 'error'

	static final ERROR = new UnsupportedOperationException()

	StatisticsHandlerImpl handler = new StatisticsHandlerImpl()

	IOutgraderProperties properties = Mock(IOutgraderProperties)

	IStatisticsExporter exporter = Mock(IStatisticsExporter)

	ExecutorService updateExecutor = Mock(ExecutorService)

	ScheduledExecutorService exportExecutor = Mock(ScheduledExecutorService)

	def setup() {
		properties.getStatisticsThreadNumber() >> UPDATE_THREAD_COUNT
		properties.getStatisticsExportPeriod() >> EXPORT_PERIOD

		handler.properties = properties
		handler.exporter = exporter
		handler.currentTimestamps.set(System.currentTimeMillis())
	}

	def "check update executor created on initialize"() {
		when:
		handler.initialize()

		then:
		handler.updateExecutor != null
	}

	def "check export executor created on initialize"() {
		when:
		handler.initialize()

		then:
		handler.exportExecutor != null
	}

	def "check update executor class"() {
		when:
		handler.initialize()

		then:
		handler.updateExecutor instanceof ThreadPoolExecutor
	}

	def "check export executor class"() {
		when:
		handler.initialize()

		then:
		handler.exportExecutor instanceof ScheduledExecutorService
	}

	def "check update executor thread number"() {
		when:
		handler.initialize()

		then:
		handler.updateExecutor.corePoolSize == UPDATE_THREAD_COUNT
	}

	def "check update executor shutdown"() {
		when:
		handler.updateExecutor = updateExecutor
		and:
		handler.finish()

		then:
		1 * updateExecutor.shutdownNow()
	}

	def "check export executor shutdown"() {
		when:
		handler.exportExecutor = exportExecutor
		and:
		handler.finish()

		then:
		1 * exportExecutor.shutdownNow()
	}

	def "check no exceptions on finish when services are null"() {
		when:
		handler.finish()

		then:
		noExceptionThrown()
	}

	def "check actions for onRequestReceived"() {
		when:
		handler.updateExecutor = updateExecutor
		and:
		handler.onRequestHandled(URI)

		then:
		1 * updateExecutor.submit(_)
	}

	def "check actions for onResponseReceived"() {
		when:
		handler.updateExecutor = updateExecutor
		and:
		handler.onResponseHandled(URI, DURATION)

		then:
		1 * updateExecutor.submit(_)
	}

	def "check actions for onAdvertismentCandidate"() {
		setup:
		handler.updateExecutor = updateExecutor

		when:
		handler.onAdvertismentCandidateFound(URI, RULE_TEXT)

		then:
		1 * updateExecutor.submit(_)
	}

	def "check actions for onError"() {
		setup:
		def source = this
		handler.updateExecutor = updateExecutor

		when:
		handler.onError(URI, source, MESSAGE, ERROR)

		then:
		1 * updateExecutor.submit(_)
	}
}
