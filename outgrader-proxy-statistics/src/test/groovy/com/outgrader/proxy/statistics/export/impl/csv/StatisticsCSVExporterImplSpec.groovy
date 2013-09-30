package com.outgrader.proxy.statistics.export.impl.csv

import org.apache.commons.io.FileUtils
import org.supercsv.io.ICsvBeanWriter

import spock.lang.Specification

import com.outgrader.proxy.core.properties.IOutgraderProperties
import com.outgrader.proxy.statistics.exceptions.StatisticsExportException
import com.outgrader.proxy.statistics.impl.StatisticsEntry
import com.outgrader.proxy.statistics.impl.StatisticsEntry.StatisticsEntryBuilder
import com.outgrader.proxy.statistics.manager.IStatisticsManager

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 *
 */
class StatisticsCSVExporterImplSpec extends Specification {

	StatisticsCSVExporterImpl exporter

	IOutgraderProperties properties = Mock(IOutgraderProperties)

	IStatisticsManager manager = Mock(IStatisticsManager)

	File exportDirectory = new File("/tmp/outgrader")

	StatisticsEntry firstEntry = new StatisticsEntryBuilder('uri', System.currentTimeMillis()).build()

	def setup() {
		exporter = Spy(StatisticsCSVExporterImpl, constructorArgs: [properties, manager])

		properties.getStatisticsExportDirectory() >> exportDirectory.getAbsolutePath()
	}

	def cleanup() {
		FileUtils.deleteQuietly(exportDirectory)
	}

	def "check writer was created"() {
		when:
		def writer = exporter.getWriter(firstEntry)

		then:
		writer != null
	}

	def "check exception occured on writer creation"() {
		when:
		exporter.getWriter(firstEntry) >> {throw new IOException() }

		and:
		exporter.exportEntry(new StatisticsEntry())

		then:
		thrown(StatisticsExportException)
	}

	def "check exception occured on writting bean"() {
		when:
		ICsvBeanWriter writer = Mock(ICsvBeanWriter)
		writer.write(_, _) >> {throw new IOException() }
		exporter.getWriter(firstEntry) >> writer
		and:
		exporter.exportEntry(new StatisticsEntry())

		then:
		thrown(StatisticsExportException)
	}

	def "check writer closed on finish"() {
		ICsvBeanWriter writer = Mock(ICsvBeanWriter)

		when:
		exporter.createWriter(firstEntry) >> writer
		exporter.getWriter(firstEntry)
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
		exporter.createWriter(firstEntry) >> writer
		exporter.getWriter(firstEntry)
		and:
		exporter.finish()

		then:
		thrown(StatisticsExportException)
	}

	def "check file created in corresponding directory"() {
		when:
		exporter.getWriter(firstEntry)

		then:
		exportDirectory.exists()
		exportDirectory.listFiles().size() > 0
	}

	def "check bean written on export"() {
		setup:
		def entry = new StatisticsEntry()
		ICsvBeanWriter writer = Mock(ICsvBeanWriter)
		exporter.getWriter(firstEntry) >> writer

		when:
		exporter.exportEntry(entry)

		then:
		1 * writer.write(entry, _)
	}
}
