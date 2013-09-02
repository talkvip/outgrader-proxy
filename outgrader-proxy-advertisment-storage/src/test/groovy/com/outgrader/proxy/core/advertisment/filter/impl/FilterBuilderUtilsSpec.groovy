package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class FilterBuilderUtilsSpec extends Specification {

	static final String SIMPLE_RULE = 'simple rule'

	static final String RULE_WITH_WILDCARD = 'rule with * wildcard'

	static final String STARTS_WITH_RULE = 'starts with rule'

	def "check simple matching filter"() {
		when:
		IFilter filter = FilterBuilderUtils.build(SIMPLE_RULE)

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == SIMPLE_RULE
	}

	def "check rule with wildcard"() {
		when:
		IFilter filter = FilterBuilderUtils.build(RULE_WITH_WILDCARD)

		then:
		filter != null
		filter instanceof AndFilter
		filter.filters.size() == 2
		filter.filters.each {  it instanceof MatchingFilter }
	}

	def "check starts with rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build('|' + STARTS_WITH_RULE)

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == '"' + STARTS_WITH_RULE
	}

	def "check ends with rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build(STARTS_WITH_RULE + '|')

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == STARTS_WITH_RULE + '"'
	}

	def "check protocol ignoring basic rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build('||' + STARTS_WITH_RULE)

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == '://' + STARTS_WITH_RULE
	}
}
