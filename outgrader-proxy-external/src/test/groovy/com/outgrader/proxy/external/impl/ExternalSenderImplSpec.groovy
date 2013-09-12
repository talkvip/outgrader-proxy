package com.outgrader.proxy.external.impl

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.DefaultHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion

import java.util.zip.GZIPInputStream

import org.apache.commons.io.Charsets
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpStatus
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import org.apache.http.protocol.HTTP

import spock.lang.Specification

import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor
import com.outgrader.proxy.external.impl.exceptions.ExternalSenderException


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class ExternalSenderImplSpec extends Specification {

	static final HEADERS = ['header1' : 'value1', 'header2' : 'value2', 'header3' : 'value3']

	HttpClient httpClient = Mock(HttpClient)

	ExternalSenderImpl sender

	HttpRequest nettyRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "uri")

	org.apache.http.HttpRequest httpRequest = new HttpGet()

	HttpResponse nettyResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)

	StatusLine statusLine = new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, 200, "statusline")

	org.apache.http.HttpResponse httpResponse = new BasicHttpResponse(statusLine)

	IAdvertismentProcessor processor = Mock(IAdvertismentProcessor)

	def setup() {
		sender = Spy(ExternalSenderImpl, constructorArgs: [processor])

		sender.client >> httpClient
	}

	def "exception should be thrown on HTTP send exception"() {
		when:
		httpClient.execute(_) >> { throw new IOException() }
		and:
		sender.send(nettyRequest)

		then:
		thrown(ExternalSenderException)
	}

	def "exception should be thrown if HTTP Client returned NULL response"() {
		when:
		httpClient.execute(_) >> null
		and:
		sender.send(nettyRequest)

		then:
		thrown(ExternalSenderException)
	}

	def "no exception should be occured if HTTP client returned response"() {
		when:
		httpClient.execute(_) >> httpResponse
		and:
		def result = sender.send(nettyRequest)

		then:
		noExceptionThrown()

		result != null
	}

	def "exception should be thrown if unsupported request method found"() {
		when:
		sender.getRequest(new HttpMethod('unknown'), 'uri')

		then:
		thrown(IllegalArgumentException)
	}

	def "check supported http request methods"(def method) {
		when:
		def value = sender.getRequest(method, 'uri')

		then:
		noExceptionThrown()
		value != null

		where:
		method << HttpMethod.methodMap.values().findAll{ methodType ->
			methodType != HttpMethod.CONNECT
		}
	}

	def "check http request contains uri"(def method) {
		when:
		def value = sender.getRequest(method, 'http://example.com')

		then:
		value.getURI().toString() == 'http://example.com'

		where:
		method << HttpMethod.methodMap.values().findAll{ methodType ->
			methodType != HttpMethod.CONNECT
		}
	}

	def "check actions on status convert"() {
		when:
		def result = sender.convertStatus(statusLine)

		then:
		result.code() == statusLine.statusCode
		result.reasonPhrase() == statusLine.reasonPhrase
	}

	def "check buffer created on NULL HttpEntity"() {
		setup:
		httpResponse.setEntity(null)

		when:
		def result = sender.processContent(_ as String, httpResponse)

		then:
		noExceptionThrown()
		result == Unpooled.EMPTY_BUFFER
	}

	def "check converted HTTPEntity content"() {
		setup:
		def content = 'some content'
		httpResponse.setEntity(new StringEntity(content))

		when:
		def result = sender.processContent(_ as String, httpResponse)

		then:
		result == Unpooled.wrappedBuffer(content.getBytes(Charsets.UTF_8))
	}

	def "check no loss on converting netty headers to http"() {
		when:
		nettyRequest.headers().set(getNettyHeaders())
		and:
		sender.copyHeaders(httpRequest, nettyRequest)

		then:
		httpRequest.getAllHeaders().length == getHTTPHeaders().size()
		getHTTPHeaders().join() == httpRequest.getAllHeaders().join()
	}

	def "check no loss on converting http headers to netty"() {
		when:
		httpResponse.setHeaders(getHTTPHeaders() as Header[])
		and:
		sender.copyHeaders(httpResponse, nettyResponse)

		then:
		nettyResponse.headers().entries().size() == getNettyHeaders().entries().size()
		getNettyHeaders().entries().join() == nettyResponse.headers().entries().join()
	}

	def getNettyHeaders() {
		HttpHeaders headers = new DefaultHttpHeaders()

		HEADERS.each { headerName, headerValue ->
			headers.add(headerName, headerValue)
		}

		headers
	}

	def getHTTPHeaders() {
		def headers = []

		HEADERS.each { headerName, headerValue ->
			headers << new BasicHeader(headerName, headerValue)
		}

		headers
	}

	def "check processor was used for OK response with text/html content"() {
		def stream = Mock(InputStream)
		def entity = Mock(HttpEntity)

		setup:
		entity.getContentType() >> new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.TEXT_HTML.getMimeType())
		httpResponse.setStatusLine(new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, HttpStatus.SC_OK, 'phrase'))

		entity.getContent() >> stream

		httpResponse.setEntity(entity)

		when:
		sender.processContent(_ as String, httpResponse)

		then:
		1 * processor.process(_ as String, stream, _)
	}

	def "check processor now used for non-ok response"(){
		def entity = new StringEntity('non-ok')

		setup:
		entity.setContentType(new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.TEXT_HTML.getMimeType()))
		httpResponse.setStatusLine(new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, HttpStatus.SC_NOT_FOUND, 'phrase'))

		httpResponse.setEntity(entity)

		when:
		def result = sender.processContent(_ as String, httpResponse)

		then:
		0 * processor.process(_, _)
		result != null
	}

	def "check processor now used for non-html response"(){
		def entity = new StringEntity('non-html')

		setup:
		entity.setContentType(new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.DEFAULT_TEXT.getMimeType()))
		httpResponse.setStatusLine(new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, HttpStatus.SC_OK, 'phrase'))

		httpResponse.setEntity(entity)

		when:
		def result = sender.processContent(_ as String, httpResponse)

		then:
		0 * processor.process(_, _, _)
		result != null
	}

	def "check exception occured on processing"() {
		def entity = new StringEntity('non-html')

		setup:
		entity.setContentType(new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.TEXT_HTML.getMimeType()))
		httpResponse.setStatusLine(new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, HttpStatus.SC_OK, 'phrase'))

		httpResponse.setEntity(entity)

		processor.process(_, _, _) >> { throw new ExternalSenderException("test") }

		when:
		sender.processContent(_ as String, httpResponse)

		then:
		thrown(ExternalSenderException)
	}

	def "check zipped response content"() {
		def entity = new StringEntity('zipped')
		def gzipStream = Mock(GZIPInputStream)

		setup:
		entity.setContentType(new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.TEXT_HTML.getMimeType()))
		entity.setContentEncoding('gzip')

		sender.gzipWrapper(_ as InputStream) >> gzipStream

		httpResponse.setStatusLine(new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, HttpStatus.SC_OK, 'phrase'))
		httpResponse.setEntity(entity)

		when:
		sender.processContent(_ as String, httpResponse)

		then:
		1 * processor.process(_ as String, gzipStream, _)
	}

	def "check non-zipped response content"() {
		def entity = new StringEntity('non-zipped')

		setup:
		entity.setContentType(new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.TEXT_HTML.getMimeType()))

		httpResponse.setStatusLine(new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, HttpStatus.SC_OK, 'phrase'))
		httpResponse.setEntity(entity)

		when:
		sender.processContent(_ as String, httpResponse)

		then:
		0 * processor.process(_ as String, _ as GZIPInputStream, _)
		1 * processor.process(_ as String, _ as InputStream, _)
	}

	def "check default charset used if response charset is null"() {
		def stream = Mock(InputStream)
		def entity = Mock(HttpEntity)
		entity.getContent() >> stream

		setup:
		entity.getContentType() >> new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, ContentType.TEXT_HTML.getMimeType())
		httpResponse.setStatusLine(new BasicStatusLine(org.apache.http.HttpVersion.HTTP_1_1, HttpStatus.SC_OK, 'phrase'))

		httpResponse.setEntity(entity)

		when:
		sender.processContent(_ as String, httpResponse)

		then:
		1 * processor.process(_ as String, stream, HTTP.DEF_CONTENT_CHARSET)
	}
}
