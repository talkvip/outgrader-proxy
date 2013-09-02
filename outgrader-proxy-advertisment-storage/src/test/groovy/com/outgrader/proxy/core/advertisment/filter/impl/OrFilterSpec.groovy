package com.outgrader.proxy.core.advertisment.filter.impl

import com.outgrader.proxy.core.advertisment.filter.IFilter;

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class OrFilterSpec extends Specification {

	final static TEST_STRING = 'test'

	OrFilter filter = new OrFilter()

	def "check filter not matches only if all subfilters returns false"() {
		setup:
		10.times {
			def subFilter = Mock(IFilter)
			subFilter.matches(_) >> false
			filter.addSubFilter(subFilter)
		}

		when:
		def result = filter.matches(TEST_STRING)

		then:
		!result
	}

	def "check filter checks all subfilters to return false"() {
		setup:
		def subFilter = Mock(IFilter)
		10.times { filter.addSubFilter(subFilter) }

		when:
		filter.matches(TEST_STRING)

		then:
		10 * subFilter.matches(TEST_STRING) >> false
	}

	def "check filter checks all subfilter until matched to return true"() {
		setup:
		def subFilter = Mock(IFilter)
		10.times {
			subFilter.matches(TEST_STRING) >> (it > 5)
			filter.addSubFilter(subFilter)
		}

		when:
		filter.matches(TEST_STRING)

		then:
		5 * subFilter.matches(TEST_STRING) >> false
		1 * subFilter.matches(TEST_STRING) >> true
	}
}
