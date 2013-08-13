package com.outgrader.proxy.core.advertisment.rule.impl

import java.util.regex.Pattern

import spock.lang.Specification

import com.outgrader.proxy.advertisment.processor.internal.ITag
import com.outgrader.proxy.advertisment.rule.IAdvertismentRule

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
	}

	def "check rule matches if pattern matches"() {
		setup:
		rule = new BasicRule(RULE_TEXT, Pattern.compile('tag'))

		when:
		def result = rule.matches(tag)

		then:
		result == true
	}

	def "check rule matches if pattern not matches"() {
		setup:
		rule = new BasicRule(RULE_TEXT, Pattern.compile('nothing'))

		when:
		def result = rule.matches(tag)

		then:
		result == false
	}
}

