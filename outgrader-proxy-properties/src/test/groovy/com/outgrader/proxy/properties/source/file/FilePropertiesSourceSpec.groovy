package com.outgrader.proxy.properties.source.file

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class FilePropertiesSourceSpec extends Specification {

	FilePropertiesSource source = Spy(FilePropertiesSource)

	def "check first not-null location selected as source"() {
		setup:
		source.getLocationCandidates() >> [
			'http://location1',
			'http://location2'
		]
		def expectedURL = new URL('http://location2')
		source.toURL('http://location2') >> expectedURL

		when:
		def result = source.getPropertiesLocation()

		then:
		result == expectedURL
	}

	def "check null location"() {
		setup:
		source.getPropertiesLocation() >> null

		when:
		source.getConfiguration()

		then:
		thrown(NullPointerException)
	}

	def "check not existing location"() {
		setup:
		source.getPropertiesLocation() >> new URL("file:/not_exists")

		when:
		source.getConfiguration()

		then:
		thrown(RuntimeException)
	}

	def "check properties file read"() {
		when:
		def result = source.getConfiguration()

		then:
		result != null
	}
}
