package com.outgrader.proxy.advertisment.processor.internal.impl.utils

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class TagUtilsSpec extends Specification {

	def "check tag without attributes"() {
		when:
		def result = TagUtils.getAttributes('<hallo>')

		then:
		result.isEmpty()
	}

	def "check tag with attributes"() {
		when:
		def result = TagUtils.getAttributes("<hallo attr='value'")

		then:
		result.isEmpty() == false
	}

	def "check tag attribute value"() {
		when:
		def result = TagUtils.getAttributes("<hallo attr='value'")

		then:
		result['attr'] == "'value'"
	}
}
