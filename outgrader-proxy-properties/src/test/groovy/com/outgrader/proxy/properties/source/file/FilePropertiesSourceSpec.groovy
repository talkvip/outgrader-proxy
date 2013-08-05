package com.outgrader.proxy.properties.source.file

import spock.lang.Specification

import com.outgrader.proxy.properties.source.IPropertiesSource

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 *
 */
class FilePropertiesSourceSpec extends Specification {

	IPropertiesSource propertiesSource = Spy(FilePropertiesSource)

	def "NPE check when no URL found"() {
		when:
		propertiesSource.getPropertiesLocation() >> null

		and:
		propertiesSource.getConfiguration()

		then:
		thrown(NullPointerException)
	}

	def "check Runtime Exception on non-properties file"() {
		when:
		propertiesSource.getPropertiesLocation() >> FilePropertiesSourceSpec.class.getResource('/com/outgrader/proxy/properties/source/file/FilePropertiesSourceSpec.class')

		and:
		propertiesSource.getConfiguration()

		then:
		thrown(RuntimeException)
	}
}
