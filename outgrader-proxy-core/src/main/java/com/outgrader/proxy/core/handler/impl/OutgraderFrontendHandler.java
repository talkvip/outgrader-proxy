package com.outgrader.proxy.core.handler.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
public class OutgraderFrontendHandler extends SimpleChannelInboundHandler<Object> implements IOutgraderFrontendHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderFrontendHandler.class);

	@Inject
	IExternalSender externalSender;

	@Inject
	IStatisticsHandler statisticsHandler;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;

			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(request.getUri());

			for (Map.Entry<String, String> header : request.headers().entries()) {
				get.addHeader(header.getKey(), header.getValue());
			}

			HttpResponse response = client.execute(get);

			HttpResponseStatus status = new HttpResponseStatus(response.getStatusLine().getStatusCode(), response.getStatusLine()
					.getReasonPhrase());

			ByteBuf content = Unpooled.EMPTY_BUFFER;
			if (response.getEntity() != null) {
				content = Unpooled.copiedBuffer(IOUtils.toByteArray(response.getEntity().getContent()));
			}

			LOGGER.debug(request.toString());
			LOGGER.debug(response.toString());

			DefaultFullHttpResponse result = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);

			for (Header header : response.getAllHeaders()) {
				result.headers().add(header.getName(), header.getValue());
			}

			ctx.writeAndFlush(result);
		}
	}
}
