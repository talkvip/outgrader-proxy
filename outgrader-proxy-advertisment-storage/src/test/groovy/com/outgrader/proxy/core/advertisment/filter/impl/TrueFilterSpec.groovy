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
class TrueFilterSpec extends Specification {

	IFilter filter

	IFilterSource filterSource = Mock(IFilterSource)

	ITag tag = Mock(ITag)

	def "check TrueRule returns true if FilterSource returns not-null value"(def source) {
		setup:
		filter = new TrueFilter(filterSource)
		filterSource.getFilterSource('uri', tag) >> source

		when:
		def result = filter.matches('uri', tag)

		then:
		result == (source != null)

		where:
		source << ['hallo', null]
	}
}
