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
	def "check rule matching"(def rule, def uri, def line, def result) {
		setup:
		IAdvertismentRuleStorage ruleStorage = createStorage(rule)
		IAdvertismentProcessor processor = createProcessor(ruleStorage)

		when:
		processor.process(uri, IOUtils.toInputStream(line), Charsets.UTF_8)

		then:
		ruleStorage.includingRules.size() > 0
		if (result) {
			1 * statistics.onAdvertismentCandidateFound(uri, rule)
		} else {
			0 * statistics.onAdvertismentCandidateFound(uri, rule)
		}

		where:
		rule       | uri   | line                                            | result
		'&ad_box_' | 'uri' | '<a href="http://reklama.by?draw&ad_box_567" />'| true
		'&ad_box_' | 'uri' | '<a href="http://reklama.by?draw&ad_box567" />' | false
		'&ad_box_' | 'uri' | '<a href="http://reklama.by?draw_ad_box_567" />'| false

		'||biz/includes/js/css-1.2.5.min.js$third-party' | 'some.uri' | '<a href="http://some.biz/includes/js/css-1.2.5.min.js" />' | true
		'||biz/includes/js/css-1.2.5.min.js$third-party' | 'some.biz' | '<a href="http://some.biz/includes/js/css-1.2.5.min.js" />' | false
		'||biz/includes/js/css-1.2.5.min.js$third-party' | 'some.uri' | '<a href="http://biz/includes/js/css-1.2.5.min.js" />'      | true

		'||24smile.$third-party,popup' | '24smile.com'    | '<a href="http://not24smile.org" />' 			   | false
		'||24smile.$third-party,popup' | '24smile.com'    | '<a href="http://24smile.org/advertisment" />'     | true
		'||24smile.$third-party,popup' | 'not24smile.com' | '<a href="http://adv.24smile.com/advertisment" />' | true
		'||24smile.$third-party,popup' | '24smile.com'    | '<a href="http://adv.24smile.com/advertisment" />' | false

		'###ads_iframe' | 'some.uri' | '<a />'                    | false
		'###ads_iframe' | 'some.uri' | '<a id="lalala" />'        | false
		'###ads_iframe' | 'some.uri' | '<a id="ads_iframe" />'    | true
		'###ads_iframe' | 'some.uri' | '<a id="no_ads_iframe" />' | false

		'##.advblock' | 'some.uri' | '<a id="advblock" />'     | true
		'##.advblock' | 'some.uri' | '<div id="advblock" />'   | true
		'##.advblock' | 'some.uri' | '<a id="not_advblock" />' | false

		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<a id="cpa_rotator_block" />'            | false
		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<div id="cpa_rotator_block" />'          | true
		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<div id="not_startcpa_rotator_block" />' | false
		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<div class="cpa_rotator_block" />'       | false
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
