package com.outgrader.proxy.it

import io.netty.buffer.Unpooled

import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentProcessorImpl
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor
import com.outgrader.proxy.core.advertisment.storage.impl.AdvertismentRuleStorageImpl
import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.core.statistics.IStatisticsHandler
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage
import com.outgrader.proxy.properties.impl.OutgraderPropertiesImpl
import com.outgrader.proxy.properties.source.file.FilePropertiesSource

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 *
 */
class TagMatchingSpec extends Specification {

	IOutgraderProperties properties = Mock(IOutgraderProperties)

	IStatisticsHandler statistics = Mock(IStatisticsHandler)

	IAdvertismentRewriter rewriter = Mock(IAdvertismentRewriter)

	def setup() {
		def source = new FilePropertiesSource()
		properties = Mock(OutgraderPropertiesImpl)
		properties.propertiesSource >> source
		properties.initialize()

		properties.advertismentListLocations >> [
			'advertisment-storage/advblock.txt',
		]
		properties.supportedTags >> ['a', 'div']

		rewriter.rewrite(_, _) >> Unpooled.EMPTY_BUFFER
		rewriter.rewrite(_, _, _, _) >> Unpooled.EMPTY_BUFFER
	}

	@Unroll("check a matching result of line #uri:#line and rule <#rule> is #result")
	def "check rule matching"(def uri, def rule, def line, def result) {
		setup:
		IAdvertismentRuleStorage ruleStorage = createStorage(rule)
		IAdvertismentProcessor processor = createProcessor(ruleStorage)

		when:
		processor.process(uri, IOUtils.toInputStream(line), Charsets.UTF_8)

		then:
		if (result) {
			1 * statistics.onAdvertismentCandidateFound(uri, rule)
		}

		where:
		uri   | rule       | line                                            | result
		'uri' | '&ad_box_' | '<a href="http://reklama.by?draw&ad_box_567" />'| true
		'uri' | '&ad_box_' | '<a href="http://reklama.by?draw&ad_box567" />' | false
		'uri' | '&ad_box_' | '<a href="http://reklama.by?draw_ad_box_567" />'| false
	}

	private IAdvertismentProcessor createProcessor(IAdvertismentRuleStorage storage) {
		new AdvertismentProcessorImpl(storage, statistics, rewriter, properties)
	}

	private IAdvertismentRuleStorage createStorage(String fileContent) {
		AdvertismentRuleStorageImpl result = Spy(AdvertismentRuleStorageImpl, constructorArgs: [properties])

		if (fileContent != null) {
			result.openRuleFileStream(_ as String) >> IOUtils.toInputStream(fileContent)
		}
		result.initializeRuleSet()

		result
	}
}