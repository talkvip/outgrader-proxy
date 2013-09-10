package com.outgrader.proxy.it.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.it.ISender;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
@Component
public class Sender implements ISender {

	private HttpClient httpClient;

	@PreDestroy
	protected void finishUp() {
		httpClient.getConnectionManager().shutdown();
	}

	@PostConstruct
	protected void initialize() {
		ClientConnectionManager connectionManager = new PoolingClientConnectionManager();

		DefaultHttpClient result = new DefaultHttpClient(connectionManager);

		httpClient = result;
	}

	@Override
	public String send(final String url) throws IOException {
		HttpGet getRequest = new HttpGet(url);

		String result = null;

		try {
			HttpResponse response = httpClient.execute(getRequest);

			result = EntityUtils.toString(response.getEntity());

			EntityUtils.consumeQuietly(response.getEntity());
		} finally {
			getRequest.releaseConnection();
		}

		return result;
	}

	@Override
	public String sendProxy(final String url, final int proxyPort) throws IOException {
		HttpHost proxy = new HttpHost("localhost", proxyPort);

		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

		try {
			return send(url);
		} finally {
			httpClient.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
	}

}
