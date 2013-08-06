package com.outgrader.proxy.statistics.export.impl

import spock.lang.Specification

import com.outgrader.proxy.statistics.exceptions.StatisticsExportException
import com.outgrader.proxy.statistics.export.impl.internal.AbstractStatisticsExporterTestImpl
import com.outgrader.proxy.statistics.impl.StatisticsEntry

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class AbstractStatisticsExporterSpec extends Specification {

	static final VALUES = [
		new StatisticsEntry(),
		new StatisticsEntry(),
		new StatisticsEntry()
	]

	AbstractStatisticsExporter exporter = Spy(AbstractStatisticsExporterTestImpl)

	def setup() {
		exporter.getStatistics() >> VALUES
	}

	def "check exporter workflow"() {
		when:
		exporter.run()

		then:
		VALUES.size() * exporter.exportEntry(_ as StatisticsEntry)
		1 * exporter.finish()
	}

	def "no exception thrown on higher level"() {
		when:
		exporter.finish() >> { throw new StatisticsExportException() }
		exporter.exportEntry(_) >> { throw new StatisticsExportException() }

		and:
		exporter.run()

		then:
		noExceptionThrown()
	}
}
