package com.outgrader.proxy.core.handler.impl

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import spock.lang.Specification

import com.outgrader.proxy.core.external.IExternalSender
import com.outgrader.proxy.core.statistics.IStatisticsHandler

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class OutgraderFrontendHandlerSpec extends Specification {

	final static URI = 'http://tut.by'

	final static HOST = 'tut.by'

	OutgraderFrontendHandler handler

	ChannelHandlerContext context = Mock(ChannelHandlerContext)

	HttpRequest request = Mock(HttpRequest)

	HttpResponse response = Mock(HttpResponse)

	IStatisticsHandler statistics = Mock(IStatisticsHandler)

	IExternalSender sender = Mock(IExternalSender)

	def setup() {
		handler = Spy(OutgraderFrontendHandler, constructorArgs: [sender, statistics])
	}

	def "check no actions on non-http request"() {
		when:
		handler.channelRead0(context, new Object())

		then:
		0 * handler.handleHttpRequest(context, _)
	}

	def "check message handled on http request"() {
		when:
		request.getUri() >> URI
		handler.channelRead0(context, request)

		then:
		1 * handler.handleHttpRequest(context, request)
	}

	def "check interaction on http request handling"() {
		when:
		handler.handleHttpRequest(context, request)

		then:
		1 * handler.getRequestURI(request)
		1 * request.getUri() >> URI
		1 * handler.handleHttpRequest(context, request)
		1 * statistics.onRequestHandled(HOST)
		1 * sender.send(HOST, request) >> response
		1 * statistics.onResponseHandled(HOST, _ as Long)
		1 * context.writeAndFlush(response)
		0 * _._
	}

	def "check a host name from request URI"() {
		setup:
		request.getUri() >> 'http://onliner.by'

		when:
		def result = handler.getRequestURI(request)

		then:
		result == 'onliner.by'
	}

	def "check a host name from headers"() {
		setup:
		HttpHeaders headers = new DefaultHttpHeaders()
		headers.add(HttpHeaders.Names.HOST, 'onliner.by')
		request.headers() >> headers
		request.getUri() >> '/image.png'

		when:
		def result = handler.getRequestURI(request)

		then:
		result == 'onliner.by'
	}
}
