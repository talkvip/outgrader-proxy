package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class NotFilterSpec extends Specification {

	final static URI = 'uri'

	ITag tag = Mock(ITag)

	def "check not filter"(def result) {
		setup:
		IFilter source = Mock(IFilter)
		source.matches(URI, tag) >> result

		def filter = new NotFilter(source)

		when:
		def isMatches = filter.matches(URI, tag)

		then:
		isMatches == !result

		where:
		result << [true, false]
	}
}
