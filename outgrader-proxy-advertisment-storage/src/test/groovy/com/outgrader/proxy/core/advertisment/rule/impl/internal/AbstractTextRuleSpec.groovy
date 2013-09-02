package com.outgrader.proxy.core.advertisment.rule.impl.internal

import spock.lang.Specification

import com.outgrader.proxy.advertisment.processor.internal.ITag
import com.outgrader.proxy.advertisment.rule.IAdvertismentRule
import com.outgrader.proxy.core.advertisment.rule.impl.internal.util.AbstractTextRuleTestImpl

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class AbstractTextRuleSpec extends Specification {

	static final String TEXT = 'pattern'

	static final String TAG_TEXT = 'tag text'

	ITag tag = Mock(ITag)

	IAdvertismentRule rule

	def setup() {
		rule = Spy(AbstractTextRuleTestImpl, constructorArgs: [TEXT])

		tag.haveAttribute(_) >> true
	}

	def "check matches uses tag text"() {
		when:
		rule.matches(tag)

		then:
		1 * tag.getText() >> TAG_TEXT
		1 * rule.matches(TAG_TEXT)
	}
}
