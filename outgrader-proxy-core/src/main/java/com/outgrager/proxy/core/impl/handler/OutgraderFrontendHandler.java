package com.outgrager.proxy.core.impl.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderFrontendHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderFrontendHandler.class);

	private Channel outboundChannel = null;

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("inbound channelActive");

		final Channel inboundChannel = ctx.channel();

		if (outboundChannel == null) {
			Bootstrap bootstrap = new Bootstrap().group(inboundChannel.eventLoop()).channel(inboundChannel.getClass())
					.handler(new OutgraderBackendHandler(inboundChannel)).option(ChannelOption.AUTO_READ, false);

			ChannelFuture future = bootstrap.connect("tut.by", 80);

			outboundChannel = future.channel();

			future.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(final ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						inboundChannel.read();
					} else {
						inboundChannel.close();
					}
				}
			});
		}
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		LOGGER.info("inbound channelRead");
		if (outboundChannel.isActive()) {
			outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(final ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						ctx.channel().read();
					} else {
						future.channel().close();
					}
				}
			});
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		cause.printStackTrace();
	}
}
