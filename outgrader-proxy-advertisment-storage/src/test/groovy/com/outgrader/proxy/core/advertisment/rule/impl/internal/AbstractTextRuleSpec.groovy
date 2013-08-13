package com.outgrader.proxy.core.advertisment.rule.impl.internal

import java.util.regex.Pattern

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.IAdvertismentRule
import com.outgrader.proxy.core.advertisment.response.internal.ITag
import com.outgrader.proxy.core.advertisment.rule.impl.internal.util.AbstractTextRuleTestImpl

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class AbstractTextRuleSpec extends Specification {

	static final String TEXT = 'pattern'

	static final Pattern PATTERN = Pattern.compile('pattern')

	static final String TAG_TEXT = 'tag text'

	ITag tag = Mock(ITag)

	IAdvertismentRule rule

	def setup() {
		rule = Spy(AbstractTextRuleTestImpl, constructorArgs: [TEXT, PATTERN])
	}

	def "check matches uses tag text"() {
		when:
		rule.matches(tag)

		then:
		1 * tag.getText() >> TAG_TEXT
		1 * rule.matches(TAG_TEXT)
	}
}
