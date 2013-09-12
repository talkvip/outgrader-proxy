package com.outgrader.proxy.statistics.manager.impl;

import java.net.URI;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
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

	private static final Function<Entry<String, InternalStatisticsEntry>, StatisticsEntry> STATISTICS_CONVERTER = new Function<Entry<String, InternalStatisticsEntry>, StatisticsEntry>() {
		@Override
		public StatisticsEntry apply(final Entry<String, InternalStatisticsEntry> input) {
			StatisticsEntryBuilder builder = new StatisticsEntryBuilder(input.getKey());

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

	private final ConcurrentHashMap<String, InternalStatisticsEntry> statistics = new ConcurrentHashMap<>();

	@Override
	public void updateStatistics(final IStatisticsEvent event) {
		String uri = event.getURI();

		HttpHost httpHost = URIUtils.extractHost(URI.create(uri));
		String host = null;
		if (httpHost == null) {
			host = uri;
		} else {
			host = httpHost.getHostName();
		}

		InternalStatisticsEntry entry = statistics.get(host);
		if (entry == null) {
			entry = new InternalStatisticsEntry();
			statistics.put(host, entry);
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

	@Override
	public Iterable<StatisticsEntry> exportStatistics() {
		return Iterables.transform(Collections.unmodifiableMap(statistics).entrySet(), STATISTICS_CONVERTER);
	}
}
