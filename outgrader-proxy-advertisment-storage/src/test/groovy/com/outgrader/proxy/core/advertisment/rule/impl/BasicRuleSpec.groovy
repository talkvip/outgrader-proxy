package com.outgrader.proxy.core.advertisment.rule.impl

import spock.lang.Specification

import com.outgrader.proxy.core.model.IAdvertismentRule
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class BasicRuleSpec extends Specification {

	static final String RULE_TEXT = 'rule'

	static final String TAG_TEXT = 'tag'

	ITag tag = Mock(ITag)

	IAdvertismentRule rule

	def setup() {
		tag.getText() >> TAG_TEXT

		tag.haveAttribute(_) >> true
	}

	def "check rule matches if pattern matches"() {
		setup:
		rule = new BasicRule(RULE_TEXT, 'tag')

		when:
		def result = rule.matches(tag)

		then:
		result == true
	}

	def "check rule matches if pattern not matches"() {
		setup:
		rule = new BasicRule(RULE_TEXT, 'nothing')

		when:
		def result = rule.matches(tag)

		then:
		result == false
	}
}

