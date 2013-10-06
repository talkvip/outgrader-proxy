package com.outgrader.proxy.it

import org.apache.commons.io.Charsets
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.ArrayUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter
import com.outgrader.proxy.advertisment.processor.impl.AdvertismentProcessorImpl
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
class TagMatchingSpec extends Specification {

	@Autowired
	IOutgraderProperties properties

	IStatisticsHandler statistics = Mock(IStatisticsHandler)

	IAdvertismentRewriter rewriter = Mock(IAdvertismentRewriter)

	def setup() {
		rewriter.rewrite(_, _) >> ArrayUtils.EMPTY_BYTE_ARRAY
		rewriter.rewrite(_, _, _, _) >> ArrayUtils.EMPTY_BYTE_ARRAY
	}

	@Unroll("check a matching result of line #uri:#line and rule <#rule> is #result")
	def "check rule matching"(def rule, def uri, def line, def result) {
		setup:
		IAdvertismentRuleStorage ruleStorage = createStorage(rule)
		IAdvertismentProcessor processor = createProcessor(ruleStorage)

		when:
		processor.process(uri, IOUtils.toInputStream(line), Charsets.UTF_8)

		then:
		ruleStorage.includingRulesVault.isEmpty() == false
		if (result) {
			1 * statistics.onAdvertismentCandidateFound(uri, rule)
		} else {
			0 * statistics.onAdvertismentCandidateFound(uri, rule)
		}

		where:
		rule | uri | line | result

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

		'##.advblock' | 'some.uri' | '<a class="advblock" />'     | true
		'##.advblock' | 'some.uri' | '<div class="advblock" />'   | true
		'##.advblock' | 'some.uri' | '<a class="not_adblock" />'  | false

		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<a id="cpa_rotator_block" />'            | false
		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<div id="cpa_rotator_block" />'          | true
		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<div id="not_startcpa_rotator_block" />' | false
		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<div class="cpa_rotator_block" />'       | false
		'##DIV[id^="cpa_rotator_block"]' | 'some.uri' | '<div id="cpa_rotator_block_block" />'    | true

		'###PopWin[onmousemove]' | 'some.uri' | '<a id="PopWin" onmousemove="dosomething" />' | true
		'###PopWin[onmousemove]' | 'some.uri' | '<a id="PopWin" />'                           | false

		'##BODY > #flydiv' | 'some.uri' | '<body><a id="flydiv" />'        | true
		'##BODY > #flydiv' | 'some.uri' | '<head><a id="flydiv" />'        | false
		'##BODY > #flydiv' | 'some.uri' | '<body><a id="something" />'     | false
		'##BODY > #flydiv' | 'some.uri' | '<body></body><a id="flydiv" />' | false

		'##NOINDEX > .search_result[class*="search_result_"]' | 'some.uri' | '<noindex><a class="search_result_" />'          | true
		'##NOINDEX > .search_result[class*="search_result_"]' | 'some.uri' | '<noindex><a class="start_search_result_end" />' | true
		'##NOINDEX > .search_result[class*="search_result_"]' | 'some.uri' | '<noindex><div class="search_result_" />'        | true
		'##NOINDEX > .search_result[class*="search_result_"]' | 'some.uri' | '<noindex><a id="search_resul" />'               | false
		'##NOINDEX > .search_result[class*="search_result_"]' | 'some.uri' | '<start><a class="search_result_" />'            | false
		'##NOINDEX > .search_result[class*="search_result_"]' | 'some.uri' | '<noindex><a type="search_result" />' 		      | false

		'www.google.com,www.google.ru##BODY > TABLE[style="border: 1px solid #369"]' | 'www.google.com' | '<body><table style="border: 1px solid #369" />' | true
		'www.google.com,www.google.ru##BODY > TABLE[style="border: 1px solid #369"]' | 'www.google.ru'  | '<body><table style="border: 1px solid #369" />' | true
		'www.google.com,www.google.ru##BODY > TABLE[style="border: 1px solid #369"]' | 'www.tut.by'     | '<body><table style="border: 1px solid #369" />' | false

		'##IMG[width="120"][height="600"]' | 'some.uri' | '<img width="120" height="600" />' | true
		'##IMG[width="120"][height="600"]' | 'some.uri' | '<img width="120" />'              | false
		'##IMG[width="120"][height="600"]' | 'some.uri' | '<img width="120" height="800" />' | false
		'##IMG[width="120"][height="600"]' | 'some.uri' | '<img height="600" />'             | false
		'##IMG[width="120"][height="600"]' | 'some.uri' | '<img width="220" height="600" />' | false

		'/ad_click/*$object' | 'some.uri' | '<object data="http://another.uri/ad_click/1" />' | true
		'/ad_click/*$object' | 'some.uri' | '<img src="http://another.uri/ad_click/" />'      | false
		'/ad_click/*$object' | 'some.uri' | '<object data="http://another.uri/ad_click" />'   | false

		'/amcu_$script' | 'some.uri' | '<script data="http://some/amcu_adv" /> ' | true
		'/amcu_$script' | 'some.uri' | '<script data="http://some/amcu" />'     | false
		'/amcu_$script' | 'some.uri' | '<object data="http://some/amcu_adv" />' | false

		'^clickunder-' | 'some.uri' | '<script data="http://uri.com/clickunder-adv" />'         | true
		'^clickunder-' | 'some.uri' | '<script data="http://uri.com?clickunder-adv" />'         | true
		'^clickunder-' | 'some.uri' | '<script data="http://uri.com?request&clickunder-adv" />' | true
		'^clickunder-' | 'some.uri' | '<script data="http://some.clickunder-adv" />'            | false
		'^clickunder-' | 'some.uri' | '<script data="http://uri.com/clickunder" />'             | false
		'^clickunder-' | 'some.uri' | '<script data="http://notclickunder-adv" />' 				| false

		'2krota.ru###dle-content > TABLE:first-child CENTER' | '2krota.ru' | "<div id='dle-content'><table cellpadding=0 cellspacing=0 width=100%><tr><td><center><a href=\"http://2krota2.ru/\" target=\"_blank\"><img src=\"/rekla/866-80.jpg\" style=\"border:none;\" /></a></center></td></tr></table>" | true

		'###center_col > div[style="font-size:14px;margin-right:0;min-height:5px"] > div[style="font-size:14px;margin:0 4px;padding:1px 5px;background:#fff8e7"]' | '' | ''| true

		'###mn #center_col > div > h2.spon:first-child + ol:last-child' | '' | ''| true

		'##div#adv_id' | 'some.uri' | '<div id="adv_id" />'    | true
		'##div#adv_id' | 'some.uri' | '<div class="adv_id" />' | false
		'##div.adv_id' | 'some.uri' | '<div id="adv_id" />'    | false
		'##div.adv_id' | 'some.uri' | '<div class="adv_id" />' | true
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
