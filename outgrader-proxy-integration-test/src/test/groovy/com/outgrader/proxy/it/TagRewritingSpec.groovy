package com.outgrader.proxy.it

import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentProcessorImpl
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentRewriterImpl
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor
import com.outgrader.proxy.core.advertisment.storage.impl.AdvertismentRuleStorageImpl
import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.core.properties.IOutgraderProperties.RewriteMode
import com.outgrader.proxy.core.statistics.IStatisticsHandler
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage
import com.outgrader.proxy.properties.impl.OutgraderPropertiesImpl
import com.outgrader.proxy.properties.source.file.FilePropertiesSource

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 *
 */
class TagRewritingSpec extends Specification {

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
		properties.supportedTags >> [
			'a',
			'div',
			'start',
			'end',
			'body',
			'something'
		]
		properties.rewriteMode >> RewriteMode.ON

		rewriter = new AdvertismentRewriterImpl(properties)
	}

	@Unroll("check a rewrite result of line #uri:#line and rule <#rule> is #result")
	def "check rule matching"(def rule, def uri, def line, def result) {
		setup:
		IAdvertismentRuleStorage ruleStorage = createStorage(rule)
		IAdvertismentProcessor processor = createProcessor(ruleStorage)

		when:
		def rewriteResult = processor.process(uri, IOUtils.toInputStream(line), Charsets.UTF_8)

		then:
		rewriteResult.toString(Charsets.UTF_8) == result


		where:
		rule | uri | line | result

		'&ad_box_' | 'uri' | '<start><a href="http://reklama.by?draw&ad_box_567" /><end>'             | '<start><end>'
		'&ad_box_' | 'uri' | '<start><a href="http://reklama.by?draw&ad_box_567"></a><end>'            | '<start><end>'
		'&ad_box_' | 'uri' | '<start><a href="http://reklama.by?draw&ad_box_567"><something></a><end>'| '<start><end>'

		'##BODY > #flydiv' | 'some.uri' | '<start><body><a id="flydiv" /></end>'              | '<start><body><end>'
		'##BODY > #flydiv' | 'some.uri' | '<start><body><a id="flydiv"></a></end>'            | '<start><body><end>'
		'##BODY > #flydiv' | 'some.uri' | '<start><body><a id="flydiv"><something></a></end>' | '<start><body><end>'
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
