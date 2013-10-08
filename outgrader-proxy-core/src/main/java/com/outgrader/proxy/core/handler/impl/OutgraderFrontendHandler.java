package com.outgrader.proxy.core.handler.impl;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderRequestException;
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
		String uri = getRequestURI(request);
		statisticsHandler.onRequestHandled(uri);

		long before = System.currentTimeMillis();
		try {
			HttpResponse response = externalSender.send(uri, request);

			ctx.writeAndFlush(response);
		} catch (Throwable e) {
			handleException(e);

			throw e;
		} finally {
			long after = System.currentTimeMillis();

			statisticsHandler.onResponseHandled(uri, after - before);
		}
	}

	protected String getRequestURI(final HttpRequest request) {
		URI uri = null;
		String requestURI = request.getUri();
		try {
			uri = URI.create(requestURI);
		} catch (IllegalArgumentException e) {
			try {
				URL url = new URL(requestURI);

				uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(),
						url.getRef());
			} catch (URISyntaxException | MalformedURLException e1) {
				statisticsHandler.onError(request.getUri(), this, e.getMessage(), e1);
			}
		}

		if ((uri != null) && uri.isAbsolute()) {
			return uri.getHost();
		}

		return request.headers().get(HttpHeaders.Names.HOST);
	}

	private void handleException(final Throwable cause) {
		String uri = StringUtils.EMPTY;
		if (cause instanceof AbstractOutgraderRequestException) {
			uri = ((AbstractOutgraderRequestException) cause).getURL();
		}

		statisticsHandler.onError(uri, this, cause.getMessage(), cause);
	}
}
