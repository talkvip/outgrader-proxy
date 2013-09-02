package com.outgrader.proxy.statistics.manager.impl;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.impl.ResponseEvent;
import com.outgrader.proxy.statistics.impl.StatisticsEntry;
import com.outgrader.proxy.statistics.impl.StatisticsEntry.StatisticsEntryBuilder;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class StatisticsManager {

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

			return builder.build();
		}
	};

	private static final class StatisticsManagerHandler {
		private static volatile StatisticsManager instance = new StatisticsManager();
	}

	private final ConcurrentHashMap<String, InternalStatisticsEntry> statistics = new ConcurrentHashMap<>();

	protected StatisticsManager() {

	}

	public static StatisticsManager getInstance() {
		return StatisticsManagerHandler.instance;
	}

	public void updateStatistics(final IStatisticsEvent event) {
		String uri = event.getURI();
		InternalStatisticsEntry entry = statistics.get(uri);
		if (entry == null) {
			entry = new InternalStatisticsEntry();
			statistics.put(uri, entry);
		}

		switch (event.getType()) {
		case REQUEST:
			entry.updateRequest();
			break;
		case RESPONSE:
			ResponseEvent responseEvent = (ResponseEvent) event;

			entry.updateResponse(responseEvent.getDuration());
			break;
		default:
			// TODO: throw exception
		}
	}

	public Iterable<StatisticsEntry> exportStatistics() {
		return Iterables.transform(Collections.unmodifiableMap(statistics).entrySet(), STATISTICS_CONVERTER);
	}
}