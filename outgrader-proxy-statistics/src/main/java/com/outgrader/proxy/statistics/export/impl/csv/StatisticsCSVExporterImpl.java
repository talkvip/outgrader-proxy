package com.outgrader.proxy.statistics.export.impl.csv;

import java.io.FileWriter;
import java.io.IOException;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.outgrader.proxy.statistics.exceptions.StatisticsExportException;
import com.outgrader.proxy.statistics.export.impl.AbstractStatisticsExporter;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class StatisticsCSVExporterImpl extends AbstractStatisticsExporter {

	private static final String[] HEADERS = { "uri", "requestCount",
			"responseCount", "minDuration", "averageDuration", "maxDuration" };

	private ICsvBeanWriter writer;

	@Override
	protected void exportEntry(final StatisticsEntry entry)
			throws StatisticsExportException {
		try {
			getWriter().write(entry, HEADERS);
		} catch (IOException e) {
			throw new StatisticsExportException(
					"An exception occured during writing statistics entry", e);
		}
	}

	protected ICsvBeanWriter getWriter() throws IOException {
		if (writer == null) {
			writer = new CsvBeanWriter(new FileWriter("statistics.csv"),
					CsvPreference.STANDARD_PREFERENCE);

			writer.writeHeader(HEADERS);
		}

		return writer;
	}

	@Override
	protected void finish() throws StatisticsExportException {
		try {
			getWriter().close();
		} catch (IOException e) {
			throw new StatisticsExportException(
					"An exception occured during closing Writer", e);
		}
	}
}
