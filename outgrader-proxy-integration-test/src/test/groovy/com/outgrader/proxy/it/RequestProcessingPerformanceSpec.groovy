package com.outgrader.proxy.it

import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpVersion

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.params.ClientPNames
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.apache.http.util.EntityUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.core.external.IExternalSender

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 *
 */
@Ignore
@ContextConfiguration(locations = 'classpath*:META-INF/*/applicationContext.xml')
class RequestProcessingPerformanceSpec extends Specification {

	static final FACTOR = 1.2

	static final DELAY = 200

	static final ATTEMPTS = 10

	static final SLEEP = 1

	@Autowired
	IExternalSender outgraderSender

	HttpClient httpClient

	def setup() {
		ClientConnectionManager connectionManager = new PoolingClientConnectionManager()

		httpClient = new DefaultHttpClient(connectionManager)
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false)
	}

	@Unroll('#featureName for #uri')
	def "check delay between outgrader and clean request"(def uri) {
		setup:
		def outgraderTimes = getRequestTimesForOutgrader(uri)
		def cleanTimes = getCleanRequestTimes(uri)

		when:
		def averageOutgrader = outgraderTimes.sum() / outgraderTimes.size() / 1000000
		def averageClean = cleanTimes.sum() / cleanTimes.size() / 1000000
		def delayDifference = averageOutgrader - averageClean

		then:
		delayDifference <= DELAY

		where:
		uri << [
			'http://www.tut.by',
			'http://www.onliner.by'
		]
	}

	@Unroll('#featureName for #uri')
	def "check delay factor between outgrader and clean request"(def uri) {
		setup:
		def outgraderTimes = getRequestTimesForOutgrader(uri)
		def cleanTimes = getCleanRequestTimes(uri)

		when:
		def averageOutgrader = outgraderTimes.sum() / outgraderTimes.size() / 1000000
		def averageClean = cleanTimes.sum() / cleanTimes.size() / 1000000
		def delayFactor = averageOutgrader/averageClean

		then:
		delayFactor <= FACTOR

		where:
		uri << [
			'http://www.tut.by',
			'http://www.onliner.by'
		]
	}

	def getRequestTimesForOutgrader(def uri) {
		HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri)

		request.headers().add(HttpHeaders.Names.USER_AGENT, 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36')

		def times = []

		ATTEMPTS.times {
			long before = System.nanoTime()

			outgraderSender.send(request)

			long after = System.nanoTime()

			times << after - before

			Thread.sleep(SLEEP * 1000) //sleep for 20 second to prevent blocking on site side
		}

		times
	}

	def getCleanRequestTimes(def uri) {
		def times = []

		ATTEMPTS.times {
			HttpGet request = new HttpGet(uri)

			long before = System.nanoTime()

			HttpResponse response = httpClient.execute(request)

			EntityUtils.toString(response.getEntity())

			long after = System.nanoTime()

			EntityUtils.consumeQuietly(response.getEntity())

			request.releaseConnection()

			times << after - before

			Thread.sleep(SLEEP * 1000) //sleep for 20 second to prevent blocking on site side
		}

		times
	}
}
