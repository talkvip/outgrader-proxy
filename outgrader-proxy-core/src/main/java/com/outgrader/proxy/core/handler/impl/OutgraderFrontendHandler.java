package com.outgrader.proxy.core.handler.impl;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.core.external.IExternalSender;
import com.outgrader.proxy.core.handler.IOutgraderFrontendHandler;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Singleton
@Sharable
public class OutgraderFrontendHandler extends SimpleChannelInboundHandler<Object> implements IOutgraderFrontendHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderFrontendHandler.class);

	@Inject
	protected IExternalSender externalSender;

	@Inject
	protected IStatisticsHandler statisticsHandler;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start channelRead0(<" + ctx + ">, <" + msg + ">)");
		}

		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else {
			LOGGER.error("Cannot handle message <" + msg + ">");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish channelRead0()");
		}
	}

	protected void handleHttpRequest(final ChannelHandlerContext ctx, final HttpRequest request) throws Exception {
		String uri = request.getUri();
		statisticsHandler.onRequestHandled(uri);

		long before = System.currentTimeMillis();
		HttpResponse response = externalSender.send(request);
		long after = System.currentTimeMillis();

		statisticsHandler.onResponseHandled(uri, after - before);

		ctx.writeAndFlush(response);
	}
}
