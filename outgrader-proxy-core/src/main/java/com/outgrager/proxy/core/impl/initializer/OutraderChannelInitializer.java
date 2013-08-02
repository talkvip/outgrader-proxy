package com.outgrager.proxy.core.impl.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import com.outgrager.proxy.core.impl.handler.OutgraderFrontendHandler;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutraderChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("http-codec", new HttpServerCodec());
		pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
		// pipeline.addLast("logger", new LoggingHandler(LogLevel.DEBUG));
		pipeline.addLast("handler", new OutgraderFrontendHandler());
	}

}
