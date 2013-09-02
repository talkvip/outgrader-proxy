package com.outgrader.proxy.core.advertisment.storage.impl

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
}
