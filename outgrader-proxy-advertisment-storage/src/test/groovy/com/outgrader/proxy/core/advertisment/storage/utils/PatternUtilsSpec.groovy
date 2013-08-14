package com.outgrader.proxy.core.advertisment.storage.utils

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 *
 */
class PatternUtilsSpec extends Specification {

	def "line should match a pattern"(def pattern, def line) {
		when:
		def compiled = PatternUtils.createPattern(pattern)

		then:
		noExceptionThrown()
		compiled != null
		line.each { exampleLine ->
			assert compiled.matcher(exampleLine).matches()
		}

		where:
		pattern << [
			'&site',
			'+adv',
			'/1/ads/*',
			'/bag?r[]='
		]
		line << [
			[
				'&site',
				'&site-end',
				'begin-&site'
			],
			[
				'+adv',
				'+adv-end',
				'begin+adv'
			],
			[
				'/1/ads/something',
				'begin/1/ads/something',
				'/1/ads/',
				'begin/1/ads/'
			],
			[
				'/bag?r[]=',
				'begin/bag?r[]=',
				'/bag?r[]=end'
			]
		]
	}
}
