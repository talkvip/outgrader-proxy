package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.advertisment.filter.IFilterSource

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class MatchingFilterSpec extends Specification {

	IFilter filter

	IFilterSource source = Mock(IFilterSource)

	@Unroll("check that #line matches #pattern with result #result")
	def "check result of matching same as result of contains"(def pattern, def line, def result) {
		setup:
		filter = new ContainsFilter(pattern, source)

		when:
		def isMatches = filter.matches(line, false)

		then:
		isMatches == result

		where:
		pattern << ['same', 'error', 'SAME']
		line << [
			'this is same line',
			'successed',
			'same'
		]
		result << [true, false, false]
	}

	@Unroll("check that #line matches (ignoring case) #pattern with result #result")
	def "check result of matching same as result of contains ignoring case"(def pattern, def line, def result) {
		setup:
		filter = new ContainsFilter(pattern, source)

		when:
		def isMatches = filter.matches(line, true)

		then:
		isMatches == result

		where:
		pattern << ['same', 'error', 'SAME']
		line << [
			'this is same line',
			'successed',
			'same'
		]
		result << [true, false, true]
	}
}

