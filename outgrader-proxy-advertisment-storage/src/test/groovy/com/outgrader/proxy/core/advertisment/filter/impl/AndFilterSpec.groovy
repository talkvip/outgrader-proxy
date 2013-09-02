package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class AndFilterSpec extends Specification {

	final static TEST_STRING = 'test'

	AndFilter filter = new AndFilter()

	def "check filter matches only if all subfilters returns true"() {
		setup:
		10.times {
			def subFilter = Mock(IFilter)
			subFilter.matches(_) >> true
			filter.addSubFilter(subFilter)
		}

		when:
		def result = filter.matches(TEST_STRING)

		then:
		result
	}

	def "check filter checks all subfilters to return true"() {
		setup:
		def subFilter = Mock(IFilter)
		10.times { filter.addSubFilter(subFilter) }

		when:
		filter.matches(TEST_STRING)

		then:
		10 * subFilter.matches(TEST_STRING) >> true
	}

	def "check filter checks all subfilter until failed to return false"() {
		setup:
		def subFilter = Mock(IFilter)
		10.times {
			subFilter.matches(TEST_STRING) >> (it < 5)
			filter.addSubFilter(subFilter)
		}

		when:
		filter.matches(TEST_STRING)

		then:
		5 * subFilter.matches(TEST_STRING) >> true
		1 * subFilter.matches(TEST_STRING) >> false
	}
}
