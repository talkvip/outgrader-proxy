package com.outgrader.proxy.core.util;

import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class OutgraderHttpContentCompressor extends HttpContentCompressor {

	@Override
	protected Result beginEncode(final HttpResponse headers, final String acceptEncoding) throws Exception {
		String contentType = headers.headers().get(HttpHeaders.Names.CONTENT_TYPE);
		String contentEncoding = headers.headers().get(HttpHeaders.Names.CONTENT_ENCODING);
		if (!contentType.contains("text/html") || StringUtils.isEmpty(contentEncoding)) {
			return null;
		}

		headers.headers().set(HttpHeaders.Names.CONTENT_ENCODING, HttpHeaders.Values.IDENTITY);

		return super.beginEncode(headers, acceptEncoding);
	}
}
