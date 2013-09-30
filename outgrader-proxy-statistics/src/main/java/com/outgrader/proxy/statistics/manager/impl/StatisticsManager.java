package com.outgrader.proxy.statistics.manager.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.impl.ResponseEvent;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;
import com.outgrader.proxy.statistics.impl.StatisticsEntry.StatisticsEntryBuilder;
import com.outgrader.proxy.statistics.manager.IStatisticsManager;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
@Component
public class StatisticsManager implements IStatisticsManager {

	private static final Function<Entry<StatisticsKey, InternalStatisticsEntry>, StatisticsEntry> STATISTICS_CONVERTER = new Function<Entry<StatisticsKey, InternalStatisticsEntry>, StatisticsEntry>() {
		@Override
		public StatisticsEntry apply(final Entry<StatisticsKey, InternalStatisticsEntry> input) {
			StatisticsEntryBuilder builder = new StatisticsEntryBuilder(input.getKey().getURL(), input.getKey().getTimestamp());

			InternalStatisticsEntry entry = input.getValue();
			builder.withAverageDuration(entry.getAverageDuration().get());
			builder.withMaxDuration(entry.getMaxDuration().get());
			builder.withMinDuration(entry.getMinDuration().get());
			builder.withRequestCount(entry.getRequestCount().get());
			builder.withResponseCount(entry.getResponseCount().get());
			builder.withErrorCount(entry.getErrorCount().get());
			builder.withAdvertismentCandidateCount(entry.getAdvertismentCandidateCount().get());

			return builder.build();
		}
	};

	private final Map<StatisticsKey, InternalStatisticsEntry> periodStatistics = new ConcurrentHashMap<>();

	private final long statisticsPeriod;

	@Inject
	public StatisticsManager(final IOutgraderProperties properties) {
		statisticsPeriod = properties.getStatisticsExportPeriod() * DateUtils.MILLIS_PER_MINUTE;
	}

	@Override
	public void updateStatistics(final IStatisticsEvent event) {
		StatisticsKey key = getKey(event);

		InternalStatisticsEntry entry = periodStatistics.get(key);
		if (entry == null) {
			entry = new InternalStatisticsEntry();
			periodStatistics.put(key, entry);
		}

		switch (event.getType()) {
		case REQUEST:
			entry.updateRequest();
			break;
		case RESPONSE:
			ResponseEvent responseEvent = (ResponseEvent) event;

			entry.updateResponse(responseEvent.getDuration());
			break;
		case ERROR:
			entry.updateError();
			break;
		case ADVERTISMENT_CANDIDATE:
			entry.updateAdvertismentCandidateCount();
			break;
		default:
			// TODO: throw exception
		}
	}

	protected String getHost(final String uri) {
		HttpHost httpHost = URIUtils.extractHost(URI.create(uri));
		String host = null;
		if (httpHost == null) {
			host = uri;
		} else {
			host = httpHost.getHostName();
		}

		return host;
	}

	protected long getPeriodTimestamp(final IStatisticsEvent event) {
		long timestamp = event.getTimestamp();

		return getPeriodTimestamp(timestamp);
	}

	protected long getPeriodTimestamp(final long currentTime) {
		long offset = currentTime % statisticsPeriod;

		return currentTime - offset;

	}

	protected StatisticsKey getKey(final IStatisticsEvent event) {
		return new StatisticsKey(getHost(event.getURI()), getPeriodTimestamp(event));
	}

	@Override
	public Iterable<StatisticsEntry> exportStatistics() {
		Map<StatisticsKey, InternalStatisticsEntry> exportedEntries = new HashMap<>();
		long statisticsTimestamp = getPeriodTimestamp(System.currentTimeMillis()) - statisticsPeriod;

		for (StatisticsKey key : periodStatistics.keySet()) {
			if (key.getTimestamp() == statisticsTimestamp) {
				exportedEntries.put(key, periodStatistics.remove(key));
			}
		}

		return Iterables.transform(exportedEntries.entrySet(), STATISTICS_CONVERTER);
	}
}
