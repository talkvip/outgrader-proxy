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

	static final String TAG_ID = 'id'

	static final String TAG_NAME = 'name'

	ITag tag = Mock(ITag)

	IFilterSource source = Mock(IFilterSource)

	def setup() {
		tag.getText() >> TAG_TEXT
		tag.getAttribute(ITag.ID_ATTRIBUTE) >> TAG_ID
		tag.getName() >> TAG_NAME
		tag.getId() >> TAG_ID
		tag.getCSSId() >> TAG_NAME + '.' + TAG_ID
	}

	def "check simple matching filter"() {
		when:
		IFilter filter = FilterBuilderUtils.build(SIMPLE_RULE, source)

		then:
		filter != null
		filter instanceof ContainsFilter
		filter.pattern == SIMPLE_RULE
	}

	def "check rule with wildcard"() {
		when:
		IFilter filter = FilterBuilderUtils.build(RULE_WITH_WILDCARD, source)

		then:
		filter != null
		filter instanceof AndFilter
		filter.filters.size() == 2
		filter.filters.each {  it instanceof ContainsFilter }
	}

	def "check starts with rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build('|' + STARTS_WITH_RULE, source)

		then:
		filter != null
		filter instanceof ContainsFilter
		filter.pattern == '"' + STARTS_WITH_RULE
	}

	def "check ends with rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build(STARTS_WITH_RULE + '|', source)

		then:
		filter != null
		filter instanceof ContainsFilter
		filter.pattern == STARTS_WITH_RULE + '"'
	}

	def "check protocol ignoring basic rule"() {
		when:
		IFilter filter = FilterBuilderUtils.build('||' + STARTS_WITH_RULE, source)

		then:
		filter != null
		filter instanceof ContainsFilter
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
		IFilterSource source = FilterBuilderUtils.BASIC_FILTER_SOURCE

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
		IFilterSource source = FilterBuilderUtils.DOMAIN_FILTER_SOURCE

		when:
		def result = source.getFilterSource(URI, tag)

		then:
		result == URI
	}

	def "check not filter builder"() {
		when:
		IFilter filter = FilterBuilderUtils.build('~' + SIMPLE_RULE, source, true)

		then:
		filter != null
		filter instanceof NotFilter
		filter.source != null
		filter.source.pattern == SIMPLE_RULE
	}

	def "check css id filter source"() {
		setup:
		IFilterSource source = FilterBuilderUtils.CSS_ID_FILTER_SOURCE

		when:
		def result = source.getFilterSource(URI, tag)

		then:
		result == TAG_ID
	}

	def "check tag name filter source"() {
		setup:
		IFilterSource source = FilterBuilderUtils.TAG_NAME_FILTER_SOURCE

		when:
		def result = source.getFilterSource(URI, tag)

		then:
		result == TAG_NAME
	}

	def "check css selector filter source"() {
		setup:
		IFilterSource source = FilterBuilderUtils.CSS_SELECTOR_FILTER_SOURCE

		when:
		def result = source.getFilterSource(URI, tag)

		then:
		result == TAG_NAME + '.' + TAG_ID
	}

	def "check tag attribute filter source"() {
		setup:
		IFilterSource source = FilterBuilderUtils.getTagAttributeFilterSource('attr')
		tag.getAttribute('attr') >> 'value'

		when:
		def result = source.getFilterSource(URI, tag)

		then:
		result == 'value'
	}

	def "check starts with filter"() {
		setup:
		IFilterSource source = FilterBuilderUtils.getTagAttributeFilterSource('attr')

		when:
		def filter = FilterBuilderUtils.buildStartsWithFilter(RULE_WITH_WILDCARD, source)

		then:
		filter != null
		filter instanceof StartsWithFilter
		filter.pattern == RULE_WITH_WILDCARD
	}

	def "check ends with filter"() {
		setup:
		IFilterSource source = FilterBuilderUtils.getTagAttributeFilterSource('attr')

		when:
		def filter = FilterBuilderUtils.buildEndsWithFilter(RULE_WITH_WILDCARD, source)

		then:
		filter != null
		filter instanceof EndsWithFilter
		filter.pattern == RULE_WITH_WILDCARD
	}

	def "check equals filter"() {
		setup:
		IFilterSource source = FilterBuilderUtils.getTagAttributeFilterSource('attr')

		when:
		def filter = FilterBuilderUtils.buildEqualsFilter(RULE_WITH_WILDCARD, source)

		then:
		filter != null
		filter instanceof EqualsFilter
		filter.pattern == RULE_WITH_WILDCARD
	}
}
