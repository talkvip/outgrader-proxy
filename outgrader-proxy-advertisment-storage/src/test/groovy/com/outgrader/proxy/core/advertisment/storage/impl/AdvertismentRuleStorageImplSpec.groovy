package com.outgrader.proxy.core.advertisment.storage.impl

import org.apache.commons.io.IOUtils

import spock.lang.Specification
import spock.lang.Unroll

import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage
import com.outgrader.proxy.properties.impl.OutgraderPropertiesImpl
import com.outgrader.proxy.properties.source.file.FilePropertiesSource

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class AdvertismentRuleStorageImplSpec extends Specification {

	IOutgraderProperties properties

	IAdvertismentRuleStorage storage

	def setup() {
		def source = new FilePropertiesSource()
		properties = Mock(OutgraderPropertiesImpl)
		properties.propertiesSource >> source
		properties.initialize()

		properties.advertismentListLocations >> [
			'advertisment-storage/advblock.txt',
			'advertisment-storage/easylist.txt'
		]
	}

	def "check storage successfully loaded from file"() {
		when:
		storage = new AdvertismentRuleStorageImpl(properties)

		then:
		noExceptionThrown()
	}

	def "check a basic rule was created"() {
		setup:
		storage = createStorage('||site*/hallo|*goodbye^')

		when:
		storage.initializeRuleSet()

		then:
		storage.includingRulesVault != null
		storage.includingRulesVault.rules != null
		storage.includingRulesVault.rules.size() == 1
		1 * storage.getBasicFilter(_ as String)
	}

	def "check exception thrown on underlying IO error"() {
		setup:
		storage = createStorage(null)

		when:
		storage.openRuleFileStream(_ as String) >> { throw new IOException() }
		and:
		storage.initializeRuleSet()

		then:
		thrown(IOException)
	}

	def "check commented lines not included in rules"() {
		setup:
		storage = createStorage('!something commented')

		when:
		storage.initializeRuleSet()

		then:
		storage.includingRulesVault != null
		storage.includingRulesVault.rules != null
		storage.includingRulesVault.rules.size() == 0
	}

	def "check extended rule parsed"() {
		setup:
		storage = createStorage('rule$domain=some.net')

		when:
		storage.initializeRuleSet()

		then:
		storage.includingRulesVault != null
		storage.includingRulesVault.rules != null
		!storage.includingRulesVault.subVaults.empty
		1 * storage.getExtendedFilter('rule$domain=some.net')
		1 * storage.getParametersFilter('domain=some.net')
		1 * storage.getBasicFilter('rule')
	}

	def "check extended rule filter"() {
		setup:
		storage = createStorage(null)

		when:
		def result = storage.getExtendedFilter('rule$domain=~something.net')

		then:
		result != null
		result.filter.filters.size() == 2
		result.filter.filters[1].pattern == 'rule'
	}

	@Unroll("check rule parsing from #line")
	def "check hiding element rule filters"(def line) {
		setup:
		storage = createStorage(line)

		when:
		storage.initializeRuleSet()

		then:
		1 * storage.getHidingElementFilter(line, true)
		storage.includingRulesVault != null
		storage.includingRulesVault.rules.size() == 1 || storage.includingRulesVault.subVaults.size() == 1

		where:
		line << [
			'##tag#id',
			'###id',
			'##*#id',
			'##tag.id',
			'##.id',
			'##tag'
		]
	}

	@Unroll("check rule parsing from #line")
	def "check hiding element rule with domains  filter"(def line) {
		setup:
		storage = createStorage(line)

		when:
		storage.initializeRuleSet()

		then:
		1 * storage.getHidingElementFilter(_, true)
		storage.includingRulesVault != null
		!storage.includingRulesVault.subVaults.empty

		where:
		line << [
			'domain.com,another.org##id',
			'domain.com##id'
		]
	}

	def "check excluding rule"() {
		setup:
		storage = createStorage("@@somerule")

		when:
		storage.initializeRuleSet()

		then:
		1 * storage.getBasicFilter(_)
		storage.excludingRules != null
		storage.excludingRules.size() == 1
	}

	@Unroll("check rule with attribute filters from <#line>")
	def "check rule with attribute filters"(def line) {
		setup:
		storage = createStorage(line)

		when:
		storage.initializeRuleSet()

		then:
		1 * storage.getHidingElementFilter(line, true)
		storage.includingRulesVault != null
		storage.includingRulesVault.subVaults.size() == 1

		where:
		line << [
			'##IMG[width="120"][height="600"]',
			'##A[href^="http://ad.adriver."]'
		]
	}

	private IAdvertismentRuleStorage createStorage(String fileContent) {
		AdvertismentRuleStorageImpl result = Spy(AdvertismentRuleStorageImpl, constructorArgs: [properties])

		if (fileContent != null) {
			result.openRuleFileStream(_ as String) >> IOUtils.toInputStream(fileContent)
		}

		result
	}
}
