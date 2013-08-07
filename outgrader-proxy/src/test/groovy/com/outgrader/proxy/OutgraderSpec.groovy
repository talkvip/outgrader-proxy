package com.outgrader.proxy

import spock.lang.Specification

import com.google.inject.Injector
import com.outgrader.proxy.core.IOutgraderProxy
import com.outgrader.proxy.core.statistics.IStatisticsHandler

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 *
 */
class OutgraderSpec extends Specification {

	Outgrader outgrader

	Injector injector = Mock(Injector)

	IOutgraderProxy proxy = Mock(IOutgraderProxy)

	IStatisticsHandler statistics = Mock(IStatisticsHandler)

	def setup() {
		outgrader = new Outgrader(injector)

		injector.getInstance(IOutgraderProxy.class) >> proxy
		injector.getInstance(IStatisticsHandler.class) >> statistics
	}

	def "check injector cachec"() {
		when:
		def attempt1 = outgrader.getInjector()
		def attempt2 = outgrader.getInjector()

		then:
		attempt1.is(attempt2)
	}

	def "check proxy starts on outgrader run"() {
		when:
		outgrader.run()

		then:
		1 * proxy.run()
	}

	def "check statistics handler initialized on outgrader run"() {
		when:
		outgrader.run()

		then:
		1 * statistics.initialize()
	}

	def "check statistics handler finished on outgrader run"() {
		when:
		outgrader.run()

		then:
		1 * statistics.finish()
	}

	def "check no more interactions except tested"() {
		when:
		outgrader.run()

		then:
		injector.getInstance(IStatisticsHandler) >> statistics
		injector.getInstance(IOutgraderProxy) >> proxy
		1 * statistics.initialize()
		1 * proxy.run()

		1 * statistics.finish()

		0 * _._
	}
}
