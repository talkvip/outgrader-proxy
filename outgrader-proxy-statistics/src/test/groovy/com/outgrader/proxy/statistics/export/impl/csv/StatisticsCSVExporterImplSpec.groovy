package com.outgrader.proxy.statistics.export.impl.csv

import org.supercsv.io.ICsvBeanWriter

import spock.lang.Specification

import com.outgrader.proxy.statistics.exceptions.StatisticsExportException
import com.outgrader.proxy.statistics.impl.StatisticsEntry

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class StatisticsCSVExporterImplSpec extends Specification {

	StatisticsCSVExporterImpl exporter = Spy(StatisticsCSVExporterImpl)

	def "check writer was created"() {
		when:
		def writer = exporter.getWriter()

		then:
		writer != null
	}

	def "check exception occured on writer creation"() {
		when:
		exporter.getWriter() >> {throw new IOException() }

		and:
		exporter.exportEntry(new StatisticsEntry())

		then:
		thrown(StatisticsExportException)
	}

	def "check exception occured on writting bean"() {
		when:
		ICsvBeanWriter writer = Mock(ICsvBeanWriter)
		writer.write(_, _) >> {throw new IOException() }
		exporter.getWriter() >> writer
		and:
		exporter.exportEntry(new StatisticsEntry())

		then:
		thrown(StatisticsExportException)
	}

	def "check writer closed on finish"() {
		ICsvBeanWriter writer = Mock(ICsvBeanWriter)

		when:
		exporter.getWriter() >> writer
		and:
		exporter.finish()

		then:
		1 * writer.close()
	}

	def "check exception occured on finishing writer"() {
		ICsvBeanWriter writer = Mock(ICsvBeanWriter)

		when:
		writer.close() >> {throw new IOException() }
		and:
		exporter.getWriter() >> writer
		and:
		exporter.finish()

		then:
		thrown(StatisticsExportException)
	}
}
