package com.outgrader.proxy.core.advertisment.filter.impl

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.advertisment.filter.IFilterSource
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class FilterBuilderUtilsSpec extends Specification {

	static final String SIMPLE_RULE = 'simple rule'

	static final String RULE_WITH_WILDCARD = 'rule with * wildcard'

	static final String STARTS_WITH_RULE = 'starts with rule'

	static final String URI = 'uri'

	static final String TAG_TEXT = 'tag'

	ITag tag = Mock(ITag)

	IFilterSource source = Mock(IFilterSource)

	def setup() {
		tag.getText() >> TAG_TEXT
	}

	def "check simple matching filter"() {
		when:
		IFilter filter = FilterBuilderUtils.build(SIMPLE_RULE, source)

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == SIMPLE_RULE
	}

	def "check rule with wildcard"() {
		when:
		IFilter filter = FilterBuilderUtils.build(RULE_WITH_WILDCARD, source)

		then:
		filter != null
		filter instanceof AndFilter
		filter.filters.size() == 2
		filter.filters.each {  it instanceof MatchingFilter }
	}

	def "check starts with rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build('|' + STARTS_WITH_RULE, source)

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == '"' + STARTS_WITH_RULE
	}

	def "check ends with rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build(STARTS_WITH_RULE + '|', source)

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == STARTS_WITH_RULE + '"'
	}

	def "check protocol ignoring basic rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build('||' + STARTS_WITH_RULE, source)

		then:
		filter != null
		filter instanceof MatchingFilter
		filter.pattern == '://' + STARTS_WITH_RULE
	}

	def "check separator rule creates amount of possible patterns"() {
		setup:
		def possiblePatterns = [
			'//rule//',
			'//rule/',
			'//rule?',
			'//rule=',
			'//rule&',
			'/rule//',
			'/rule/',
			'/rule?',
			'/rule=',
			'/rule&',
			'?rule//',
			'?rule/',
			'?rule?',
			'?rule=',
			'?rule&',
			'=rule//',
			'=rule/',
			'=rule?',
			'=rule=',
			'=rule&',
			'&rule//',
			'&rule/',
			'&rule?',
			'&rule=',
			'&rule&'
		]
		when:
		IFilter filter = FilterBuilderUtils.build('^rule^', source)

		then:
		filter != null
		filter instanceof OrFilter
		possiblePatterns.each { pattern ->
			def contains = false

			filter.filters.each {
				if (it instanceof OrFilter) {
					it.filters.each {
						if (it.pattern == pattern) {
							contains = true
						}
					}
				} else if (it.pattern == pattern) {
					contains = true
				}
			}

			assert contains
		}
	}

	def "check basic filter souce"() {
		setup:
		IFilterSource source = FilterBuilderUtils.getBasicFilterSource()

		when:
		def result = source.getFilterSource(URI, tag)

		then:
		result == tag.getText()
	}

	def "check filters joining with and"() {
		setup:
		IFilter filter = Mock(IFilter)

		when:
		IFilter result = FilterBuilderUtils.joinAnd([filter]* 10)

		then:
		result != null
		result instanceof AndFilter
		result.filters.size() == 10
		result.filters.each { assert it == filter }
	}

	def "check domain filter source"() {
		setup:
		IFilterSource source = FilterBuilderUtils.getDomainFilterSource()

		when:
		def result = source.getFilterSource(URI, tag)

		then:
		result == URI
	}

	def "check not filter builder"() {
		when:
		IFilter filter = FilterBuilderUtils.build('~' + SIMPLE_RULE, source)

		then:
		filter != null
		filter instanceof NotFilter
		filter.source != null
		filter.source.pattern == SIMPLE_RULE
	}
}
