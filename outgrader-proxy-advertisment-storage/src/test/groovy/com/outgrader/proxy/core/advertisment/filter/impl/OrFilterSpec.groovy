package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class OrFilterSpec extends Specification {

	final static URI = 'test'

	ITag tag = Mock(ITag)

	OrFilter filter = new OrFilter()

	def "check filter not matches only if all subfilters returns false"() {
		setup:
		10.times {
			def subFilter = Mock(IFilter)
			subFilter.matches(_) >> false
			filter.addSubFilter(subFilter)
		}

		when:
		def result = filter.matches(URI, tag)

		then:
		!result
	}

	def "check filter checks all subfilters to return false"() {
		setup:
		def subFilter = Mock(IFilter)
		10.times { filter.addSubFilter(subFilter) }

		when:
		filter.matches(URI, tag)

		then:
		10 * subFilter.matches(URI, tag) >> false
	}

	def "check filter checks all subfilter until matched to return true"() {
		setup:
		def subFilter = Mock(IFilter)
		10.times {
			subFilter.matches(URI, tag) >> (it > 5)
			filter.addSubFilter(subFilter)
		}

		when:
		filter.matches(URI, tag)

		then:
		5 * subFilter.matches(URI, tag) >> false
		1 * subFilter.matches(URI, tag) >> true
	}

	def "check NPE on adding null filter"() {
		when:
		filter.addSubFilter(null)

		then:
		thrown(NullPointerException)
	}
}
