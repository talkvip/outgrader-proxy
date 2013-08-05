package com.outgrader.proxy.core.impl

import spock.lang.Specification

import com.outgrader.proxy.core.properties.IOutgraderProperties

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 *
 */
class OutgraderProxyImplSpec extends Specification {

	IOutgraderProperties properties = Mock(IOutgraderProperties.class)

	def "check exception on null array of ports"() {
	}
}
