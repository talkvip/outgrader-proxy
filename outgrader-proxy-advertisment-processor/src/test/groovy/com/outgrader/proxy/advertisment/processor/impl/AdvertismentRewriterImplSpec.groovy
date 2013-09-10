package com.outgrader.proxy.advertisment.processor.impl

import java.nio.charset.Charset

import org.apache.commons.io.Charsets

import spock.lang.Specification

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.internal.TagReader
import com.outgrader.proxy.core.model.IAdvertismentRule
import com.outgrader.proxy.core.model.ITag
import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.core.properties.IOutgraderProperties.RewriteMode

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class AdvertismentRewriterImplSpec extends Specification {

	static final Charset CHARSET = Charsets.UTF_8

	static final String TAG_TEXT = 'some tag text'

	IOutgraderProperties properties = Mock(IOutgraderProperties)

	IAdvertismentRewriter rewriter

	ITag tag = Mock(ITag)

	IAdvertismentRule rule = Mock(IAdvertismentRule)

	TagReader tagReader = Mock(TagReader)

	def setup() {
		tag.getText() >> TAG_TEXT
	}

	def "check unchangable rewrite"() {
		setup:
		rewriter = new AdvertismentRewriterImpl(properties)

		when:
		def result = rewriter.rewrite(tag, CHARSET)

		then:
		result != null
		result.toString(CHARSET) == TAG_TEXT
	}

	def "check no adv. rewrite if rewrite mode is OFF"() {
		setup:
		properties.rewriteMode >> RewriteMode.OFF
		rewriter = Spy(AdvertismentRewriterImpl, constructorArgs: [properties])

		when:
		rewriter.rewrite(tag, rule, CHARSET, tagReader)

		then:
		0 * rule._
		0 * tagReader._
		1 * rewriter.rewrite(tag, CHARSET)
	}
}
