package com.outgrader.proxy.core.handler.impl;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

import javax.inject.Inject;

import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.core.external.IExternalSender;
import com.outgrader.proxy.core.handler.IOutgraderFrontendHandler;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Component
@Sharable
public class OutgraderFrontendHandler extends SimpleChannelInboundHandler<Object> implements IOutgraderFrontendHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderFrontendHandler.class);

	private final IExternalSender externalSender;

	private final IStatisticsHandler statisticsHandler;

	@Inject
	public OutgraderFrontendHandler(final IExternalSender externalSender, final IStatisticsHandler statisticsHandler) {
		this.externalSender = externalSender;
		this.statisticsHandler = statisticsHandler;
	}

	@Profiled
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start channelRead0(<" + ctx + ">, <" + msg + ">)");
		}

		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else {
			if (!msg.equals(LastHttpContent.EMPTY_LAST_CONTENT)) {
				LOGGER.error("Cannot handle message <" + msg + ">");
			}
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

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		statisticsHandler.onError("", this, cause.getMessage(), cause);

		super.exceptionCaught(ctx, cause);
	}
}
