package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.advertisment.filter.IFilterSource
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 *
 */
class ContainsDomainFilterSpec extends Specification {

	IFilter filter

	IFilterSource filterSource = Mock(IFilterSource)

	ITag tag = Mock(ITag)

	def "check ContainsDomainFilter returns true if filter source contains uri of request"(def source, def isMatches) {
		setup:
		filter = new ContainsDomainFilter(filterSource)
		filterSource.getFilterSource('tut.by', tag) >> source

		when:
		def result = filter.matches('tut.by', tag)

		then:
		result == isMatches

		where:
		source | isMatches
		'http://tut.by/blablabla' | true
		'http://something.net/nothing' | false
	}
}
