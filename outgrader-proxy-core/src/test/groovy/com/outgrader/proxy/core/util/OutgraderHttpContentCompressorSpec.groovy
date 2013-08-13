package com.outgrader.proxy.core.util

import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpContentCompressor
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class OutgraderHttpContentCompressorSpec extends Specification {

	static final ACCEPTED_ENCODING = "gzip, deflate"

	HttpContentCompressor compressor = new OutgraderHttpContentCompressor()

	def "check non-html request not processed by compressor"() {
		setup:
		def response = generateResponse(['Content-Type' : 'text/xml'])

		when:
		def result = compressor.beginEncode(response, ACCEPTED_ENCODING)

		then:
		result == null
	}

	def "check html non-encoded request not processed by compressor"() {
		setup:
		def response = generateResponse(['Content-Type' : 'text/html'])

		when:
		def result = compressor.beginEncode(response, ACCEPTED_ENCODING)

		then:
		result == null
	}

	def "check html encoded request processed by compressor"() {
		setup:
		def response = generateResponse(['Content-Type' : 'text/html', 'Content-Encoding' : 'gzip'])

		when:
		def result = compressor.beginEncode(response, ACCEPTED_ENCODING)

		then:
		result != null
	}

	private HttpResponse generateResponse(Map<String, String> headers) {
		DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)

		headers.each { key, value ->
			response.headers().add(key, value)
		}

		response
	}
}
