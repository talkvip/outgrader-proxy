package com.outgrader.proxy.it

import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.advertisment.processor.internal.TagReader

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.11-SNAPSHOT
 *
 */
class TagReaderSpec extends Specification {

	HttpClient httpClient

	def setup() {
		httpClient = HttpClients.createDefault()
	}

	def cleanup() {
		httpClient.close()
	}

	@Unroll("check tag reader didn't change html content for #uri")
	def "check tag reader didn't change html content"(def uri) {
		setup:
		HttpGet get = new HttpGet(uri)
		HttpResponse response = httpClient.execute(get)

		def content = EntityUtils.toString(response.getEntity())

		when:
		def tagReaderResult = readWithTagReader(content)

		then:
		content == tagReaderResult

		cleanup:
		get.releaseConnection()

		where:
		uri << [
			'http://www.tut.by',
			'http://www.onliner.by',
			'http://habrahabr.ru/'
		]
	}

	private String readWithTagReader(String content) {
		TagReader reader = new TagReader(IOUtils.toInputStream(content), Charsets.UTF_8)

		StringBuilder builder = new StringBuilder()

		while (reader.hasNext()) {
			builder.append(reader.next())
		}

		return builder.toString()
	}
}