package com.outgrader.proxy.core.util;

import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class OutgraderHttpContentCompressor extends HttpContentCompressor {

	@Override
	protected Result beginEncode(final HttpResponse headers, final String acceptEncoding) throws Exception {
		String contentType = headers.headers().get(HttpHeaders.Names.CONTENT_TYPE);
		if (!contentType.contains("text/html")) {
			return null;
		}

		headers.headers().add(HttpHeaders.Names.CONTENT_ENCODING, HttpHeaders.Values.IDENTITY);

		return super.beginEncode(headers, acceptEncoding);
	}
}
