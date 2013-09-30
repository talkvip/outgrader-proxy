package com.outgrader.proxy.external.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor;
import com.outgrader.proxy.core.exceptions.AbstractOutgraderRequestException;
import com.outgrader.proxy.core.external.IExternalSender;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.external.impl.exceptions.ExternalSenderException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Component
public class ExternalSenderImpl implements IExternalSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSenderImpl.class);

	private final IAdvertismentProcessor responseProcessor;

	private HttpClient httpClient;

	private final IStatisticsHandler statisticsHandler;

	@Inject
	public ExternalSenderImpl(final IAdvertismentProcessor responseProcessor, final IStatisticsHandler statisticsHandler) {
		this.responseProcessor = responseProcessor;
		this.statisticsHandler = statisticsHandler;
	}

	@Override
	public HttpResponse send(final HttpRequest request) throws AbstractOutgraderRequestException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start send(<" + request + ">)");
		}

		String uri = request.getUri();

		HttpRequestBase externalRequest = getRequest(request.getMethod(), uri);

		copyHeaders(externalRequest, request);
		copyContent(externalRequest, request);

		org.apache.http.HttpResponse response = null;
		HttpResponse result = null;

		try {
			response = getClient().execute(externalRequest);

			if (response != null) {
				result = convertResponse(uri, response, request.getProtocolVersion());
			} else {
				LOGGER.error("HttpClient returned NULL for URI <" + uri + ">");
				throw new ExternalSenderException(uri, "Got a NULL response");
			}
		} catch (IOException e) {
			LOGGER.error("HttpClient throwed exception for URI <" + uri + ">");
			throw new ExternalSenderException(uri, "An exception occured during connection to external host", e);
		} finally {
			externalRequest.releaseConnection();
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("finish send() -> " + result);
		}

		return result;
	}

	private HttpResponse convertResponse(final String uri, final org.apache.http.HttpResponse response, final HttpVersion httpVersion)
			throws IOException, AbstractOutgraderRequestException {
		HttpResponse result = new DefaultFullHttpResponse(httpVersion, convertStatus(response.getStatusLine()), processContent(uri,
				response));

		copyHeaders(response, result);

		return result;
	}

	protected void copyHeaders(final org.apache.http.HttpResponse external, final HttpResponse target) {
		for (Header header : external.getAllHeaders()) {
			target.headers().add(header.getName(), header.getValue());
		}
	}

	protected void copyContent(final HttpRequestBase externalRequest, final HttpRequest request) {
		ByteBufHolder byteBufHolder = null;
		HttpEntityEnclosingRequest entityRequest = null;
		if (request instanceof ByteBufHolder) {
			byteBufHolder = (ByteBufHolder) request;
		}
		if (externalRequest instanceof HttpEntityEnclosingRequest) {
			entityRequest = (HttpEntityEnclosingRequest) externalRequest;
		}

		if ((byteBufHolder != null) && (entityRequest != null)) {
			entityRequest.setEntity(new ByteArrayEntity(byteBufHolder.content().array()));
		}
	}

	protected InputStream gzipWrapper(final InputStream stream) throws IOException {
		return new GZIPInputStream(stream);
	}

	protected ByteBuf processContent(final String uri, final org.apache.http.HttpResponse response) throws IOException,
			AbstractOutgraderRequestException {
		if (response.getEntity() != null) {
			int code = response.getStatusLine().getStatusCode();
			ContentType contentType = ContentType.get(response.getEntity());
			Header contentEncoding = response.getEntity().getContentEncoding();

			ByteBuf content = null;

			if ((code == HttpStatus.SC_OK) && contentType.getMimeType().equals(ContentType.TEXT_HTML.getMimeType())) {
				boolean zipped = (contentEncoding != null) && contentEncoding.getValue().contains("gzip");

				InputStream stream = response.getEntity().getContent();
				if (zipped) {
					stream = gzipWrapper(stream);
				}

				Charset charset = contentType.getCharset();
				if (charset == null) {
					charset = HTTP.DEF_CONTENT_CHARSET;
				}

				content = responseProcessor.process(uri, stream, charset);
			} else {
				content = Unpooled.wrappedBuffer(IOUtils.toByteArray(response.getEntity().getContent()));
			}

			EntityUtils.consume(response.getEntity());

			return content;
		}

		return Unpooled.EMPTY_BUFFER;
	}

	protected HttpResponseStatus convertStatus(final StatusLine status) {
		return new HttpResponseStatus(status.getStatusCode(), status.getReasonPhrase());
	}

	@PreDestroy
	protected void finishUp() {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@PostConstruct
	protected void initializeHttpClient() {
		ClientConnectionManager connectionManager = new PoolingClientConnectionManager();

		DefaultHttpClient result = new DefaultHttpClient(connectionManager);
		result.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);

		result.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {

			@Override
			public long getKeepAliveDuration(final org.apache.http.HttpResponse response, final HttpContext context) {
				long keepAlive = super.getKeepAliveDuration(response, context);
				if (keepAlive == -1) {
					keepAlive = 30000;
				}
				return keepAlive;
			}

		});

		httpClient = result;
	}

	protected HttpClient getClient() {
		return httpClient;
	}

	protected void copyHeaders(final HttpRequestBase external, final HttpRequest original) {
		for (Map.Entry<String, String> header : original.headers().entries()) {

			if (!header.getKey().equals(HTTP.CONTENT_LEN) && !header.getKey().equals(HTTP.TRANSFER_ENCODING)) {
				external.addHeader(header.getKey(), header.getValue());
			}
		}
	}

	protected HttpRequestBase getRequest(final HttpMethod method, final String uri) {
		HttpRequestBase result = null;

		if (method.equals(HttpMethod.GET)) {
			result = new HttpGet();
		} else if (method.equals(HttpMethod.POST)) {
			result = new HttpPost();
		} else if (method.equals(HttpMethod.DELETE)) {
			result = new HttpDelete();
		} else if (method.equals(HttpMethod.HEAD)) {
			result = new HttpHead();
		} else if (method.equals(HttpMethod.OPTIONS)) {
			result = new HttpOptions();
		} else if (method.equals(HttpMethod.PATCH)) {
			result = new HttpPatch();
		} else if (method.equals(HttpMethod.PUT)) {
			result = new HttpPut();
		} else if (method.equals(HttpMethod.TRACE)) {
			result = new HttpTrace();
		} else {
			throw new IllegalArgumentException("Unsupported HTTP Method <" + method + "> for <" + uri + ">");
		}

		try {
			URL url = new URL(uri);

			result.setURI(new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url
					.getRef()));
		} catch (URISyntaxException | MalformedURLException e) {
			statisticsHandler.onError(uri, this, e.getMessage(), e);
		}

		return result;
	}
}
