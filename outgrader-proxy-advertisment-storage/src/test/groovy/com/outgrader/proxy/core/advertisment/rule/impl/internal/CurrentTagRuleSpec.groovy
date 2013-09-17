package com.outgrader.proxy.core.advertisment.rule.impl.internal

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.model.IAdvertismentRule

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.8-SNAPSHOT
 *
 */
class CurrentTagRuleSpec extends Specification {

	IAdvertismentRule rule

	def setup() {
		rule = new CurrentTagRule("some rule", Mock(IFilter))
	}

	def "check is tag a start of rewriting"() {
		when:
		def isRewritable = rule.isRewritable(startTag, currentTag)

		then:
		isRewritable == true
	}
}
