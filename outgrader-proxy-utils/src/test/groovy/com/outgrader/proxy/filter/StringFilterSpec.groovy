package com.outgrader.proxy.filter

import spock.lang.Specification

import com.ougrader.proxy.filter.StringFilter

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class StringFilterSpec extends Specification {


	StringFilter filter = new StringFilter()

	def setup(){
		filter.addCondition("hallo")
		filter.addCondition("hi")
		filter.addCondition("hack")
		filter.addCondition("homo")
	}

	def "check word matching"(def word) {
		when:
		def result = filter.matches(word)

		then:
		result

		where:
		word << ['halloween', 'anhi']
	}
}
