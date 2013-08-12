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

	def "check remove operation not supported"() {
		when:
		createTagReader("lalala").remove()

		then:
		thrown(UnsupportedOperationException)
	}

	def "check tag collection size on line with more than one block size"() {
		when:
		def result = createTagReader("<tag01>" * 100).toList()

		then:
		result.size() == 100
	}

	def "check input stream was closed"() {
		setup:
		def stream = Mock(InputStream)

		when:
		def reader = new TagReader(stream, Charsets.UTF_8)
		and:
		reader.close()

		then:
		1 * stream.close()
	}

	def "check tag collection size on line with more than one block size with spaces"() {
		when:
		def result = createTagReader("  <tag01>" * 100).toList()

		then:
		result.size() == 200
	}

	def "check no exception but no hasNext if error occured"() {
		setup:
		def stream = Mock(InputStream)
		stream.read(_, _) >> { throw new IOException() }

		when:
		def reader = new TagReader(stream, Charsets.UTF_8)
		and:
		def hasNext = reader.hasNext()

		then:
		noExceptionThrown()
		!hasNext
	}

	def "check hasNext was called"() {
		setup:
		def reader = Spy(TagReader, constructorArgs: [
			IOUtils.toInputStream("some line"),
			Charsets.UTF_8
		])

		when:
		reader.next()

		then:
		1 * reader.hasNext()
	}

	def "check nosuchelement exception"() {
		setup:
		def reader = Spy(TagReader, constructorArgs: [
			IOUtils.toInputStream("some line"),
			Charsets.UTF_8
		])

		when:
		reader.next()

		then:
		1 * reader.hasNext() >> false
		thrown(NoSuchElementException)
	}


	private TagReader createTagReader(String line) {
		return new TagReader(IOUtils.toInputStream(line), Charsets.UTF_8)
	}
}
