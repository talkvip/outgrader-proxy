package com.outgrader.proxy.core.advertisment.rule.impl.internal

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.core.advertisment.filter.IFilter
import com.outgrader.proxy.core.model.IAdvertismentRule
import com.outgrader.proxy.core.model.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.8-SNAPSHOT
 *
 */
class CurrentTagRuleSpec extends Specification {

	ITag startTag = Mock(ITag)
	ITag currentTag = Mock(ITag)

	IAdvertismentRule rule
	IFilter filter = Mock(IFilter)

	def setup() {
		rule = new CurrentTagRule("some rule", filter)
	}

	@Unroll("check isRuleStarted returned #isMatches when filter returns #isMatches")
	def "check rule started when filter matches"(def isMatches) {
		setup:
		filter.matches(_, _) >> isMatches

		when:
		def result = rule.isRuleStarted(_ as String, _ as ITag)

		then:
		result == isMatches

		where:
		isMatches << [true, false]
	}

	@Unroll("check isRuleContinues returned #isEquals when tag.equals returns #isEquals")
	def "check rule continues"(def isEquals) {
		setup:
		startTag.equals(currentTag) >> isEquals

		when:
		def isRewritable = rule.isRuleContinues(startTag, currentTag)

		then:
		isRewritable == isEquals

		where:
		isEquals << [true, false]
	}

	@Unroll("check isRuleRewriteStarted returned #isEquals when tag.equals returns #isEquals")
	def "check rule rewrite started"(def isEquals) {
		setup:
		startTag.equals(currentTag) >> isEquals

		when:
		def isRewritable = rule.isRuleRewriteStarted(startTag, currentTag)

		then:
		isRewritable == isEquals

		where:
		isEquals << [true, false]
	}

	@Unroll("check isRuleRewriteContinues returned #isEquals when tag.parent.equals returns not #isEquals")
	def "check rule rewrite continues"(def isEquals) {
		setup:
		ITag parent = Mock(ITag)
		currentTag.openingTag >> parent
		parent.equals(startTag) >> isEquals

		when:
		def isRewritable = rule.isRuleRewriteContinues(startTag, currentTag)

		then:
		isRewritable == !isEquals

		where:
		isEquals << [true, false]
	}
}
