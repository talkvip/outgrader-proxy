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

	OutgraderPropertiesImpl properties = new OutgraderPropertiesImpl()

	Configuration config = Mock(Configuration)

	IPropertiesSource source = Mock(IPropertiesSource)

	def setup() {
		properties.propertiesSource = source
	}

	def "check configuration initializes only once"()  {
		when:
		source.getConfiguration() >> config
		and:
		def firstAttempt = properties.getConfiguration()
		def secondAttempt = properties.getConfiguration()

		then:
		firstAttempt == secondAttempt
	}
}
