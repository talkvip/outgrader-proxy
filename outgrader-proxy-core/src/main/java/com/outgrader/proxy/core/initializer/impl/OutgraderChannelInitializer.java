package com.outgrader.proxy.core.initializer.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.core.handler.IOutgraderFrontendHandler;
import com.outgrader.proxy.core.initializer.IOutgraderChannelInitializer;
import com.outgrader.proxy.core.util.OutgraderHttpContentCompressor;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Component
public class OutgraderChannelInitializer extends ChannelInitializer<SocketChannel> implements IOutgraderChannelInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderChannelInitializer.class);

	@Inject
	private IOutgraderFrontendHandler frontendHandler;

	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start initChannel(<" + ch + ">)");
		}

		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("http-codec", new HttpServerCodec());
		pipeline.addLast("http-aggregator", new HttpObjectAggregator(4086));
		pipeline.addLast("http-compressor", new OutgraderHttpContentCompressor());
		pipeline.addLast("handler", frontendHandler);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish initChannel()");
		}
	}
}
