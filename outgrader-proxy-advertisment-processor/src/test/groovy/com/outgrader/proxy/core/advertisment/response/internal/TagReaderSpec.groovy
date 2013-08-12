package com.outgrader.proxy.core.advertisment.response.internal

import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class TagReaderSpec extends Specification {

	def "check a tag was parsed"() {
		when:
		def result = createTagReader("<tag>").first()

		then:
		result != null
	}

	def "check exceptions when no tag start found"() {
		when:
		createTagReader("tag").first()

		then:
		thrown(TagReaderException)
	}

	def "check exception when no end tag found"() {
		when:
		createTagReader("<tag").first()

		then:
		thrown(TagReaderException)
	}


	private TagReader createTagReader(String line) {
		return new TagReader(IOUtils.toInputStream(line), Charsets.UTF_8)
	}
}
