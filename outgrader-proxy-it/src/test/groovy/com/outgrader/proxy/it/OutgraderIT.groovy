package com.outgrader.proxy.it

import java.util.concurrent.Executors

import javax.inject.Inject

import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.advertisment.processor.internal.TagReader
import com.outgrader.proxy.core.IOutgraderProxy
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor
import com.outgrader.proxy.core.properties.IOutgraderProperties

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
@ContextConfiguration(locations = ["classpath*:META-INF/*/applicationContext.xml"])
class OutgraderIT extends Specification {

	@Inject
	IAdvertismentProcessor processor

	@Inject
	IOutgraderProperties properties

	@Inject
	ApplicationContext context

	@Inject
	ISender sender

	@Inject
	IOutgraderProxy proxy

	def setup() {
		runProxy()
	}

	@Unroll("full content check between original request and proxied request for #url")
	def "check content check between original request and proxied request"(def url) {
		setup: "${url}"
		def originalContent = sender.send(url)

		when:
		def proxiedContent = sender.sendProxy(url, properties.getPort())

		then:
		checkDifference(originalContent, proxiedContent)

		where:
		url << Configuration.instance.getURLList()
	}

	@Unroll("full content check between original content and processed content for #url")
	def "check content is same for proxied request and non-proxied request"(def url) {
		setup: "${url}"
		def originalContent = sender.send(url)

		when:
		def proxiedContent = processor.process(url, IOUtils.toInputStream(originalContent), Charsets.UTF_8).toString(Charsets.UTF_8)

		then:
		checkDifference(originalContent, proxiedContent)

		where:
		url << Configuration.instance.getURLList()
	}

	@Unroll("TagReader test for #url")
	def "check tag reader result"(def url) {
		setup:
		def content = sender.send(url)

		when:
		TagReader reader = new TagReader(IOUtils.toInputStream(content), Charsets.UTF_8)

		then:
		reader.hasNext()
		def last = reader.last()
		last.name == 'html'

		where:
		url << Configuration.instance.getURLList()
	}

	void checkDifference(String original, String proxied) {
		def originalIterator = IOUtils.lineIterator(IOUtils.toInputStream(original), Charsets.UTF_8)
		def proxiedIterator = IOUtils.lineIterator(IOUtils.toInputStream(proxied), Charsets.UTF_8)

		println original
		println proxied

		while (originalIterator.hasNext() && proxiedIterator.hasNext()) {
			def originalLine = originalIterator.next()
			def proxiedLine = proxiedIterator.next()

			assert originalLine == proxiedLine
		}

		assert original == proxied
	}

	def runProxy() {
		def service = Executors.newSingleThreadExecutor()
		def runner = { task -> service.submit(task as Runnable) }

		runner { proxy.run() }
	}
}
