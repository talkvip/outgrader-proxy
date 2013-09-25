package com.outgrader.proxy.it


import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentProcessorImpl
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentRewriterImpl
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor
import com.outgrader.proxy.core.advertisment.storage.impl.AdvertismentRuleStorageImpl
import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.core.statistics.IStatisticsHandler
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.10-SNAPSHOT
 *
 */
@ContextConfiguration(locations = 'classpath*:META-INF/*/applicationContext.xml')
class TagRewritingSpec extends Specification {

	@Autowired
	IOutgraderProperties properties

	IStatisticsHandler statistics = Mock(IStatisticsHandler)

	IAdvertismentRewriter rewriter = Mock(IAdvertismentRewriter)

	def setup() {
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

		'&ad_box_' | 'uri' | '<script><a href="http://reklama.by?draw&ad_box_567" /><object>'       | '<script><object>'
		'&ad_box_' | 'uri' | '<script><a href="http://reklama.by?draw&ad_box_567"></a><object>'     | '<script><object>'
		'&ad_box_' | 'uri' | '<script><a href="http://reklama.by?draw&ad_box_567"><img></a><object>'| '<script><object>'

		'##BODY > #flydiv' | 'some.uri' | '<script><body><a id="flydiv" /></object>'        | '<script><body></object>'
		'##BODY > #flydiv' | 'some.uri' | '<script><body><a id="flydiv"></a></object>'      | '<script><body></object>'
		'##BODY > #flydiv' | 'some.uri' | '<script><body><a id="flydiv"><img></a></object>' | '<script><body></object>'
		'##BODY > #flydiv' | 'some.uri' | '<script><body><img></a></object>' 				| '<script><body><img></a></object>'
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
