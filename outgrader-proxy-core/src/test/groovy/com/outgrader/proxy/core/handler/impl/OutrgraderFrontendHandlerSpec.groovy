package com.outgrader.proxy.core.handler.impl

import io.netty.channel.ChannelHandlerContext
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
class OutrgraderFrontendHandlerSpec extends Specification {

	final static URI = 'uri'

	OutgraderFrontendHandler handler = Spy(OutgraderFrontendHandler)

	ChannelHandlerContext context = Mock(ChannelHandlerContext)

	HttpRequest request = Mock(HttpRequest)

	HttpResponse response = Mock(HttpResponse)

	IStatisticsHandler statistics = Mock(IStatisticsHandler)

	IExternalSender sender = Mock(IExternalSender)

	def setup() {
		handler.statisticsHandler = statistics
		handler.externalSender = sender
	}

	def "check no actions on non-http request"() {
		when:
		handler.channelRead0(context, new Object())

		then:
		0 * handler.handleHttpRequest(context, _)
	}

	def "check message handled on http request"() {
		when:
		handler.channelRead0(context, request)

		then:
		1 * handler.handleHttpRequest(context, request)
	}

	def "check interaction on http request handling"() {
		when:
		handler.handleHttpRequest(context, request)

		then:
		1 * request.getUri() >> URI
		1 * handler.handleHttpRequest(context, request)
		1 * statistics.onRequestHandled(URI)
		1 * sender.send(request) >> response
		1 * statistics.onResponseHandled(URI, _ as Long)
		1 * context.writeAndFlush(response)
		0 * _._
	}
}
