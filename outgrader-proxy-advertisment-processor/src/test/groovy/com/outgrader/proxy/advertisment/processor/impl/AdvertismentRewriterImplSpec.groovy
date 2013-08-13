package com.outgrader.proxy.advertisment.processor.impl

import java.nio.charset.Charset

import org.apache.commons.io.Charsets

import spock.lang.Specification

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.internal.ITag

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class AdvertismentRewriterImplSpec extends Specification {

	static final Charset CHARSET = Charsets.UTF_8

	static final String TAG_TEXT = 'some tag text'

	IAdvertismentRewriter rewriter = new AdvertismentRewriterImpl()

	ITag tag = Mock(ITag)

	def setup() {
		tag.getText() >> TAG_TEXT
	}

	def "check unchangable rewrite"() {
		when:
		def result = rewriter.rewrite(tag, CHARSET)

		then:
		result != null
		result.toString(CHARSET) == TAG_TEXT
	}
}
