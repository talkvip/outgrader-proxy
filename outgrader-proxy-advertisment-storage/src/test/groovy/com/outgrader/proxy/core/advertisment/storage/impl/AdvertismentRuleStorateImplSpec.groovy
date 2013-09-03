package com.outgrader.proxy.core.advertisment.storage.impl

import org.apache.commons.io.IOUtils

import spock.lang.Specification

import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage
import com.outgrader.proxy.properties.impl.OutgraderPropertiesImpl
import com.outgrader.proxy.properties.source.file.FilePropertiesSource

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class AdvertismentRuleStorateImplSpec extends Specification {

	IOutgraderProperties properties

	IAdvertismentRuleStorage storage

	def "check storage successfully loaded from file"() {
		setup:
		def source = new FilePropertiesSource()
		properties = new OutgraderPropertiesImpl(source)

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
		storage.rules != null
		storage.rules.size() == 1
		1 * storage.getBasicFilter(_ as String)
	}

	def "check exception thrown on underlying IO error"() {
		setup:
		storage = createStorage(null)

		when:
		storage.openRuleFileStream() >> { throw new IOException() }
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
		storage.rules != null
		storage.rules.size() == 0
	}

	def "check extended rule parsed"() {
		setup:
		storage = createStorage('rule$match-case')

		when:
		storage.initializeRuleSet()

		then:
		storage.rules != null
		storage.rules.size() == 1
		1 * storage.getExtendedFilter('rule$match-case')
		1 * storage.getParametersFilter('match-case')
		1 * storage.getBasicFilter('rule')
	}

	def "check extended rule filter"() {
		setup:
		storage = createStorage(null)

		when:
		def result = storage.getExtendedFilter('rule$domain=~something.net')

		then:
		result != null
		result.filters.size() == 2
		result.filters[0].filters.size() == 1
		result.filters[1].pattern == 'rule'
	}

	private IAdvertismentRuleStorage createStorage(String fileContent) {
		properties = Mock(IOutgraderProperties)

		AdvertismentRuleStorageImpl result = Spy(AdvertismentRuleStorageImpl, constructorArgs: [properties])

		if (fileContent != null) {
			result.openRuleFileStream() >> IOUtils.toInputStream(fileContent)
		}

		result
	}
}
