package com.outgrader.proxy.advertisment.processor.impl

import io.netty.buffer.Unpooled

import java.nio.charset.Charset

import org.apache.commons.io.Charsets

import spock.lang.Specification

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.internal.TagReader
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor
import com.outgrader.proxy.core.model.IAdvertismentRule
import com.outgrader.proxy.core.model.ITag
import com.outgrader.proxy.core.model.ITag.TagType
import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.core.statistics.IStatisticsHandler
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage

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

	IOutgraderProperties properties = Mock(IOutgraderProperties)

	IAdvertismentProcessor processor

	TagReader tagReader

	def setup() {
		processor = Spy(AdvertismentProcessorImpl, constructorArgs: [
			ruleStorage,
			statisticsHandler,
			rewriter,
			properties
		])

		tagReader = Mock(TagReader, constructorArgs: [stream, CHARSET])
		processor.createTagReader(stream, CHARSET) >> tagReader

		tagReader.iterator() >> [tag].iterator()

		processor.createTagReader(stream, CHARSET) >> tagReader

		ruleStorage.getIncludingRules() >> [rule]

		rewriter.rewrite(_, _) >> Unpooled.EMPTY_BUFFER
		rewriter.rewrite(_, _, _) >> Unpooled.EMPTY_BUFFER
		rewriter.rewrite(tag, rule, _, _) >> Unpooled.EMPTY_BUFFER
	}

	def "check all tags was read"() {
		when:
		tagReader.iterator() >> ([tag]* 10).iterator()

		and:
		processor.process(URI, stream, CHARSET)

		then:
		1 * processor.process(_, _, _)
		10 * processor.isAnalysable(_ as ITag)
	}

	def "check no actions if tag is not analysable"() {
		when:
		processor.process(URI, stream, CHARSET)

		then:
		0 * rule._(_)
	}

	def "check rule checked if tag is analysable"() {
		when:
		processor.isAnalysable(_ as ITag) >> true

		and:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rule.isRuleStarted(URI, tag)
	}

	def "check statistics updated on rule matching"() {
		setup:
		rule.toString() >> 'some string'
		rule.isRuleStarted(URI, tag) >> true

		when:
		processor.isAnalysable(tag) >> true

		and:
		processor.process(URI, stream, CHARSET)

		then:
		1 * statisticsHandler.onAdvertismentCandidateFound(URI, rule.toString())
	}

	def "check no statistics update if rule not matches"() {
		when:
		processor.isAnalysable(tag) >> true

		and:
		processor.process(URI, stream, CHARSET)
		and:
		rule.isRuleStarted(tag) >> false

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
		1 * statisticsHandler.onError(URI, processor, _ as String, exception)
	}

	def "check rewriter on empty tag"() {
		setup:
		processor.isAnalysable(tag) >> false

		when:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rewriter.rewrite(tag, CHARSET) >> Unpooled.EMPTY_BUFFER
	}

	def "check rewriter on non-matched tag"() {
		setup:
		processor.isAnalysable(tag) >> true
		rule.isRuleStarted(tag) >> false

		when:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rewriter.rewrite(tag, CHARSET) >> Unpooled.EMPTY_BUFFER
	}

	def "check rewriter on matched tag"() {
		setup:
		processor.isAnalysable(tag) >> true
		tag.tagType >> TagType.OPEN_AND_CLOSING
		rule.isRuleStarted(URI, tag) >> true

		when:
		processor.process(URI, stream, CHARSET)

		then:
		1 * rewriter.rewrite(tag, rule, CHARSET, tagReader) >> Unpooled.EMPTY_BUFFER
	}

	def "check tag not analasyable if it's not anylysable by property"() {
		when:
		tag.analysable >> false

		then:
		!processor.isAnalysable(tag)
	}

	def "check tag not analysable if it's CLOSED tag"() {
		setup:
		tag.analysable >> true

		when:
		tag.tagType >> TagType.CLOSING

		then:
		!processor.isAnalysable(tag)
	}

	def "check tag not analysable if it's not in supported tags"() {
		setup:
		tag.analysable >> true
		tag.tagType >> TagType.OPENING

		when:
		properties.getSupportedTags() >> ['tag']
		and:
		tag.getName() >> 'not a tag'

		then:
		!processor.isAnalysable(tag)
	}

	def "check tag is analysable when conditions satisfied"() {
		setup:
		tag.analysable >> true
		tag.tagType >> TagType.OPENING
		properties.getSupportedTags() >> ['tag']
		tag.getName() >> 'tag'

		when:
		def result = processor.isAnalysable(tag)

		then:
		result
	}
}

