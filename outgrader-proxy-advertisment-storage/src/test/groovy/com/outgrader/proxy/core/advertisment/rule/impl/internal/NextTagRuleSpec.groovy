package com.outgrader.proxy.core.advertisment.rule.impl.internal

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.model.IAdvertismentRule
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.8-SNAPSHOT
 *
 */
class NextTagRuleSpec extends Specification {

	ITag startTag = Mock(ITag)
	ITag currentTag = Mock(ITag)

	IAdvertismentRule rule

	def setup() {
		rule = new NextTagRule("some rule", Mock(IFilter))
	}
}
