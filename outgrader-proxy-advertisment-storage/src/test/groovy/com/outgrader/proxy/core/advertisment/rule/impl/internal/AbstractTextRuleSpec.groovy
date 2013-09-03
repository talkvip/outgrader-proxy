package com.outgrader.proxy.core.advertisment.rule.impl.internal

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.advertisment.rule.impl.internal.util.AbstractTextRuleTestImpl
import com.outgrader.proxy.core.model.IAdvertismentRule
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class AbstractTextRuleSpec extends Specification {

	static final String TEXT = 'pattern'

	static final String TAG_TEXT = 'tag text'

	static final String URI = 'uri'

	ITag tag = Mock(ITag)

	IFilter filter = Mock(IFilter)

	IAdvertismentRule rule

	def setup() {
		rule = Spy(AbstractTextRuleTestImpl, constructorArgs: [TEXT, filter])
	}

	def "check matches uses tag text"() {
		when:
		rule.matches(URI, tag)

		then:
		1 * tag.getText() >> TAG_TEXT
		1 * rule.matches(TAG_TEXT)
	}
}
