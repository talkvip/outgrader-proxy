package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class NotFilterSpec extends Specification {

	final static TEST_STRING = 'test'

	def "check not filter"(def result) {
		setup:
		IFilter source = Mock(IFilter)
		source.matches(TEST_STRING) >> result

		def filter = new NotFilter(source)

		when:
		def isMatches = filter.matches(TEST_STRING)

		then:
		isMatches == !result

		where:
		result << [true, false]
	}
}
