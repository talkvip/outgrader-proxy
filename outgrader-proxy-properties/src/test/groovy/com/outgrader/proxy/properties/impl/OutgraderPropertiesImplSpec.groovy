package com.outgrader.proxy.properties.impl

import org.apache.commons.configuration.Configuration

import spock.lang.Specification

import com.outgrader.proxy.properties.source.IPropertiesSource

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class OutgraderPropertiesImplSpec extends Specification {

	OutgraderPropertiesImpl properties

	Configuration config = Mock(Configuration)

	IPropertiesSource source = Mock(IPropertiesSource)

	def setup() {
		properties = new OutgraderPropertiesImpl(source)
	}

	def "check configuration initializes only once"()  {
		when:
		source.getConfiguration() >> config
		and:
		def firstAttempt = properties.getConfiguration()
		def secondAttempt = properties.getConfiguration()

		then:
		firstAttempt != null
		secondAttempt != null
		firstAttempt == secondAttempt
	}

	def "check properties initialization"() {
		when:
		source.getConfiguration() >> config
		and:
		properties.initialize()

		then:
		1 * source.getConfiguration() >> config
		properties.getConfiguration().is(config)
	}

	def "check NPE if Configuration is null"() {
		when:
		source.getConfiguration() >> null
		and:
		properties.initialize()

		then:
		1 * source.getConfiguration()
		thrown(NullPointerException)
	}

	def "check all properties came from config"(def method) {
		setup:
		source.getConfiguration() >> config

		when: "call method <${method}>"
		properties."get${method.capitalize()}"()

		then:
		1 * config./get.*/(_)

		where:
		method << [
			'port',
			'bossThreadNumber',
			'workerThreadNumber',
			'statisticsThreadNumber',
			'statisticsExportPeriod',
			'statisticsExportDirectory',
			'advertismentListLocation'
		]
	}
}
