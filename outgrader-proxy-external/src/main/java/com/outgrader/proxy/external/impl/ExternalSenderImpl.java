package com.outgrader.proxy.external.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;
import com.outgrader.proxy.core.external.IExternalSender;
import com.outgrader.proxy.external.impl.exceptions.ExternalSenderException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@NotThreadSafe
public class ExternalSenderImpl implements IExternalSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSenderImpl.class);

	private HttpClient client;

	@Override
	public HttpResponse send(final HttpRequest request) throws AbstractOutgraderException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start send(<" + request + ">)");
		}

		HttpRequestBase externalRequest = getRequest(request.getMethod(), request.getUri());

		copyHeaders(externalRequest, request);

		org.apache.http.HttpResponse response = null;
		HttpResponse result = null;

		try {
			response = getClient().execute(externalRequest);

			if (response != null) {
				result = convertResponse(response, request.getProtocolVersion());
			}
		} catch (IOException e) {
			throw new ExternalSenderException("An exception occured during connection to external host", e);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish send() -> " + result);
		}

		return result;
	}

	private HttpResponse convertResponse(final org.apache.http.HttpResponse response, final HttpVersion httpVersion) throws IOException {
		HttpResponse result = new DefaultFullHttpResponse(httpVersion, convertStatus(response.getStatusLine()),
				convertContent(response.getEntity()));

		copyHeaders(response, result);

		return result;
	}

	private void copyHeaders(final org.apache.http.HttpResponse external, final HttpResponse target) {
		for (Header header : external.getAllHeaders()) {
			target.headers().add(header.getName(), header.getValue());
		}
	}

	private ByteBuf convertContent(final HttpEntity entity) throws IOException {
		if (entity != null) {
			return Unpooled.copiedBuffer(IOUtils.toByteArray(entity.getContent()));
		}

		return Unpooled.EMPTY_BUFFER;
	}

	private HttpResponseStatus convertStatus(final StatusLine status) {
		return new HttpResponseStatus(status.getStatusCode(), status.getReasonPhrase());
	}

	private HttpClient getClient() {
		if (client == null) {
			client = new DefaultHttpClient();
		}

		return client;
	}

	private void copyHeaders(final HttpRequestBase external, final HttpRequest original) {
		for (Map.Entry<String, String> header : original.headers().entries()) {
			external.addHeader(header.getKey(), header.getValue());
		}
	}

	private HttpRequestBase getRequest(final HttpMethod method, final String uri) {
		if (method.equals(HttpMethod.GET)) {
			return new HttpGet(uri);
		} else if (method.equals(HttpMethod.POST)) {
			return new HttpPost(uri);
		}

		return null;
	}

}
