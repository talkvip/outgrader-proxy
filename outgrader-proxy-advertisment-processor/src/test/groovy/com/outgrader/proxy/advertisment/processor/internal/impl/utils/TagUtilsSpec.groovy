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

	def "check tag attribute value"() {
		when:
		def result = TagUtils.getAttributes('<hallo attr="value" />')

		then:
		result.isEmpty() == false
		result['attr'] == 'value'
	}

	def "check tag text with spaces"() {
		when:
		def result = TagUtils.getAttributes('<div title="Hallo World!" />')

		then:
		result['title'] == 'Hallo World!'
	}

	def "check tag multiple attribute values"() {
		when:
		def result = TagUtils.getAttributes('<hallo attr1="value1" attr2="value2" />')

		then:
		result['attr1'] == 'value1'
		result['attr2'] == 'value2'
	}
}
