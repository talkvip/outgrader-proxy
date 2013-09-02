package com.outgrader.proxy.statistics.export.impl.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.statistics.exceptions.StatisticsExportException;
import com.outgrader.proxy.statistics.export.impl.AbstractStatisticsExporter;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StatisticsCSVExporterImpl extends AbstractStatisticsExporter {

	private static final String[] HEADERS = { "uri", "requestCount", "responseCount", "minDuration", "averageDuration", "maxDuration" };

	private ICsvBeanWriter writer;

	private final IOutgraderProperties properties;

	@Inject
	public StatisticsCSVExporterImpl(final IOutgraderProperties properties) {
		this.properties = properties;
	}

	@Override
	protected void exportEntry(final StatisticsEntry entry) throws StatisticsExportException {
		try {
			getWriter().write(entry, HEADERS);
		} catch (IOException e) {
			throw new StatisticsExportException("An exception occured during writing statistics entry", e);
		}
	}

	protected ICsvBeanWriter getWriter() throws IOException {
		if (writer == null) {
			File output = new File(properties.getStatisticsExportDirectory(), "statistics.csv");

			if (!output.exists()) {
				FileUtils.forceMkdir(output.getParentFile());
				output.createNewFile();
			}

			writer = new CsvBeanWriter(new FileWriter(output), CsvPreference.STANDARD_PREFERENCE);

			writer.writeHeader(HEADERS);
		}

		return writer;
	}

	@Override
	protected void finish() throws StatisticsExportException {
		try {
			getWriter().close();
		} catch (IOException e) {
			throw new StatisticsExportException("An exception occured during closing Writer", e);
		} finally {
			writer = null;
		}
	}
}
