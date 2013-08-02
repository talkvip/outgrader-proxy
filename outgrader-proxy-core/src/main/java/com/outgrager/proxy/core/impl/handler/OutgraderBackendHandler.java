package com.outgrager.proxy.core.impl.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderBackendHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderFrontendHandler.class);

	private final Channel channel;

	public OutgraderBackendHandler(final Channel inboundChannel) {
		channel = inboundChannel;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("outbound channelActive");
		ctx.read();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		LOGGER.info("outbound channelRead");
		channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {

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

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		cause.printStackTrace();
	}

}
