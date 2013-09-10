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
		properties.initialize()
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

	def "check IllegalArgumentException if rewriteMode is null"() {
		setup:
		source.getConfiguration() >> config
		config.getString(OutgraderPropertiesImpl.PROXY_REWRITE_MODE) >> null

		when:
		properties.initialize()
		and:
		properties.rewriteMode

		then:
		thrown(IllegalArgumentException)
	}

	def "check IllegalArgumentException if rewriteMode value is not of enum"() {
		setup:
		source.getConfiguration() >> config
		config.getString(OutgraderPropertiesImpl.PROXY_REWRITE_MODE) >> 'lalala'

		when:
		properties.initialize()
		and:
		properties.rewriteMode

		then:
		thrown(IllegalArgumentException)
	}

	def "check all properties came from config"(def method) {
		setup:
		source.getConfiguration() >> config
		properties.initialize()

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
			'advertismentListLocations',
			'supportedTags',
			'rewriteMode'
		]
	}
}
