package com.outgrader.proxy.advertisment.processor.impl

import io.netty.buffer.Unpooled

import java.nio.charset.Charset

import org.apache.commons.io.Charsets

import spock.lang.Specification

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.internal.ITag
import com.outgrader.proxy.advertisment.processor.internal.TagReader
import com.outgrader.proxy.advertisment.rule.IAdvertismentRule
import com.outgrader.proxy.advertisment.storage.IAdvertismentRuleStorage
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor
import com.outgrader.proxy.core.statistics.IStatisticsHandler

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class AdvertismentProcessorImplSpec extends Specification {

	static final Charset CHARSET = Charsets.UTF_8

	static final String URI = 'uri'

	IAdvertismentRuleStorage ruleStorage = Mock(IAdvertismentRuleStorage)

	IAdvertismentRule rule = Mock(IAdvertismentRule)

	IStatisticsHandler statisticsHandler = Mock(IStatisticsHandler)

	ITag tag = Mock(ITag)

	InputStream stream = Mock(InputStream)

	IAdvertismentRewriter rewriter = Mock(IAdvertismentRewriter)

	IAdvertismentProcessor processor

	TagReader tagReader

	def setup() {
		processor = Spy(AdvertismentProcessorImpl, constructorArgs: [
			ruleStorage,
			statisticsHandler,
			rewriter
		])

		tagReader = Mock(TagReader, constructorArgs: [stream, CHARSET])
		processor.createTagReader(stream, CHARSET) >> tagReader

		tagReader.iterator() >> [tag].iterator()

		processor.createTagReader(stream, CHARSET) >> tagReader

		ruleStorage.getRules() >> [rule]

		rewriter.rewrite(_, _) >> Unpooled.EMPTY_BUFFER
		rewriter.rewrite(_, _, _) >> Unpooled.EMPTY_BUFFER
	}

	def "check all tags was read"() {
		when:
		tagReader.iterator() >> ([tag]* 10).iterator()

		and:
		processor.process(URI, stream, CHARSET)

		then:
		1 * processor.process(_, _, _)
		10 * tag.isAnalysable()
	}

	def "check no actions if tag is not analysable"() {
		when:
		tag.analysable >> false
		and:
		processor.process(URI, stream, CHARSET)

		then:
		0 * rule._(_)
	}

	def "check rule checked if tag is analysable"() {
		when:
		tag.analysable >> true

		and:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rule.matches(tag)
	}

	def "check statistics updated on rule matching"() {
		setup:
		rule.toString() >> 'some string'
		rule.matches(tag) >> true

		when:
		tag.analysable >> true

		and:
		processor.process(URI, stream, CHARSET)

		then:
		1 * statisticsHandler.onAdvertismentCandidateFound(URI, rule.toString())
	}

	def "check no statistics update if rule not matches"() {
		when:
		tag.analysable >> true

		and:
		processor.process(URI, stream, CHARSET)
		and:
		rule.matches(tag) >> false

		then:
		0 * statisticsHandler._
	}

	def "check error handling"() {
		setup:
		def exception = new IOException('some string')

		when:
		tagReader.close() >> { throw exception }

		and:
		processor.process(URI, stream, CHARSET)

		then:
		1 * statisticsHandler.onError(processor, _ as String, exception)
	}

	def "check rewriter on empty tag"() {
		setup:
		tag.analysable >> false

		when:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rewriter.rewrite(tag, CHARSET) >> Unpooled.EMPTY_BUFFER
	}

	def "check rewriter on non-matched tag"() {
		setup:
		tag.analysable >> true
		rule.matches(tag) >> false

		when:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rewriter.rewrite(tag, CHARSET) >> Unpooled.EMPTY_BUFFER
	}

	def "check rewriter on matched tag"() {
		setup:
		tag.analysable >> true
		rule.matches(tag) >> true

		when:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rewriter.rewrite(tag, rule, CHARSET) >> Unpooled.EMPTY_BUFFER
	}

	def "check default charset is used when no charset given"() {
		when:
		processor.process(URI, stream, null)

		then:
		1 * processor.createTagReader(stream, Charset.defaultCharset()) >> tagReader
	}
}
