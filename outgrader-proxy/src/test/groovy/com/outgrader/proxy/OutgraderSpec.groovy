package com.outgrader.proxy

import org.springframework.context.ApplicationContext

import spock.lang.Specification

import com.outgrader.proxy.core.IOutgraderProxy
import com.outgrader.proxy.core.statistics.IStatisticsHandler

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 *
 */
class OutgraderSpec extends Specification {

	Outgrader outgrader

	ApplicationContext context = Mock(ApplicationContext)

	IOutgraderProxy proxy = Mock(IOutgraderProxy)

	IStatisticsHandler statistics = Mock(IStatisticsHandler)

	def setup() {
		outgrader = new Outgrader(context)

		context.getBean(IOutgraderProxy.class) >> proxy
		context.getBean(IStatisticsHandler.class) >> statistics
	}

	def "check injector cache"() {
		when:
		def attempt1 = outgrader.getApplicationContext()
		def attempt2 = outgrader.getApplicationContext()

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
		context.getBean(IStatisticsHandler) >> statistics
		context.getBean(IOutgraderProxy) >> proxy
		1 * statistics.initialize()
		1 * proxy.run()

		1 * statistics.finish()

		0 * _._
	}
}
