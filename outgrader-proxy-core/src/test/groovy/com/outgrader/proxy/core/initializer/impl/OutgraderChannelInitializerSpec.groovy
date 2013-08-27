package com.outgrader.proxy.core.initializer.impl

import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpContentCompressor
import io.netty.handler.codec.http.HttpServerCodec
import spock.lang.Specification

import com.outgrader.proxy.core.handler.IOutgraderFrontendHandler

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class OutgraderChannelInitializerSpec extends Specification {

	OutgraderChannelInitializer initializer = new OutgraderChannelInitializer()

	ChannelPipeline pipeline = Mock(ChannelPipeline)

	SocketChannel channel = Mock(SocketChannel)

	IOutgraderFrontendHandler handler = Mock(IOutgraderFrontendHandler)

	def setup() {
		initializer.frontendHandler = handler
	}

	def "check channel handlers"() {
		when:
		initializer.initChannel(channel)

		then:
		1 * pipeline.addLast(_ as String, _ as IOutgraderFrontendHandler)
		1 * pipeline.addLast(_ as String, _ as HttpServerCodec)
		1 * pipeline.addLast(_ as String, _ as HttpContentCompressor)

		1 * channel.pipeline() >> pipeline

		0 * _._
	}
}
