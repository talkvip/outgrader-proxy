package com.outgrader.proxy.core.advertisment.filter.impl

import com.outgrader.proxy.core.advertisment.filter.IFilter;

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class MatchingPatternSpec extends Specification {

	IFilter filter

	def "check result of matching same as result of contains"(def pattern, def line, def result) {
		setup:
		filter = new MatchingFilter(pattern)

		when:
		def isMatches = filter.matches(line)

		then:
		isMatches == result

		where:
		pattern << ['same', 'error']
		line << [
			'this is same line',
			'successed'
		]
		result << [true, false]
	}
}
