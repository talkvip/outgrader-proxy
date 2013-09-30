package com.outgrader.proxy.statistics.export.impl.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.statistics.exceptions.StatisticsExportException;
import com.outgrader.proxy.statistics.export.impl.AbstractStatisticsExporter;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;
import com.outgrader.proxy.statistics.manager.IStatisticsManager;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
@Component
public class StatisticsCSVExporterImpl extends AbstractStatisticsExporter {

	private static final String[] HEADERS = { "uri", "requestCount", "advertismentCandidateCount", "responseCount", "minDuration",
			"averageDuration", "maxDuration", "errorCount" };

	private static final String FILE_NAME_PATTERN = "statictics {0}.csv";

	private final Map<Long, ICsvBeanWriter> writers = new HashMap<>();

	private final IOutgraderProperties properties;

	private final DateFormat periodDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@Inject
	public StatisticsCSVExporterImpl(final IOutgraderProperties properties, final IStatisticsManager manager) {
		super(manager);
		this.properties = properties;
	}

	@Override
	protected void exportEntry(final StatisticsEntry entry) throws StatisticsExportException {
		try {
			getWriter(entry).write(entry, HEADERS);
		} catch (IOException e) {
			throw new StatisticsExportException("An exception occured during writing statistics entry", e);
		}
	}

	protected ICsvBeanWriter createWriter(final StatisticsEntry entry) throws IOException {
		File output = new File(properties.getStatisticsExportDirectory(), collectName(entry.getPeriodTimestamp()));

		if (!output.exists()) {
			FileUtils.forceMkdir(output.getParentFile());
			output.createNewFile();
		}

		return new CsvBeanWriter(new FileWriter(output), CsvPreference.STANDARD_PREFERENCE);
	}

	protected ICsvBeanWriter getWriter(final StatisticsEntry entry) throws IOException {
		ICsvBeanWriter writer = writers.get(entry.getPeriodTimestamp());

		if (writer == null) {
			writer = createWriter(entry);

			writer.writeHeader(HEADERS);

			writers.put(entry.getPeriodTimestamp(), writer);
		}

		return writer;
	}

	private String collectName(final long period) {
		return MessageFormat.format(FILE_NAME_PATTERN, periodDateFormat.format(new Date(period)));
	}

	@Override
	protected void finish() throws StatisticsExportException {
		try {
			for (ICsvBeanWriter writer : writers.values()) {
				writer.close();
			}
		} catch (IOException e) {
			throw new StatisticsExportException("An exception occured during closing Writer", e);
		} finally {
			writers.clear();
		}
	}
}
