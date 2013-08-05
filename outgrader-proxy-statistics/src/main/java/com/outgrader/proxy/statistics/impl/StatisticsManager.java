package com.outgrader.proxy.statistics.impl;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.outgrader.proxy.statistics.events.IStatisticsEvent;
import com.outgrader.proxy.statistics.events.impl.ResponseEvent;
import com.outgrader.proxy.statistics.impl.StatisticsEntry.StatisticsEntryBuilder;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class StatisticsManager {

	/**
	 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
	 * @since 0.2.0-SNAPSHOT
	 * 
	 */
	private static final class STATISTICS_CONVERTER implements Function<Entry<String, InternalStatisticsEntry>, StatisticsEntry> {
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
	}

	private static final class StatisticsManagerHandler {
		private static volatile StatisticsManager instance = new StatisticsManager();
	}

	static class InternalStatisticsEntry {

		private final AtomicInteger requestCount = new AtomicInteger();

		private final AtomicInteger responseCount = new AtomicInteger();

		private AtomicLong minDuration = null;

		private final AtomicLong maxDuration = new AtomicLong();

		private final AtomicLong averageDuration = new AtomicLong();

		public void updateRequest() {
			requestCount.incrementAndGet();
		}

		public void updateResponse(final long newDuration) {
			if (minDuration == null) {
				minDuration = new AtomicLong(newDuration);
			}
			long min = minDuration.get();
			long max = maxDuration.get();
			long average = averageDuration.get();
			int count = responseCount.get();

			if (newDuration > max) {
				maxDuration.compareAndSet(max, newDuration);
			}
			if (newDuration < min) {
				minDuration.compareAndSet(min, newDuration);
			}

			averageDuration.set(((average * count) + newDuration) / (count + 1));

			responseCount.incrementAndGet();
		}

		public AtomicInteger getRequestCount() {
			return requestCount;
		}

		public AtomicInteger getResponseCount() {
			return responseCount;
		}

		public AtomicLong getMinDuration() {
			return minDuration;
		}

		public AtomicLong getMaxDuration() {
			return maxDuration;
		}

		public AtomicLong getAverageDuration() {
			return averageDuration;
		}

	}

	private final ConcurrentHashMap<String, InternalStatisticsEntry> statistics = new ConcurrentHashMap<>();

	private StatisticsManager() {

	}

	public static StatisticsManager getInstance() {
		return StatisticsManagerHandler.instance;
	}

	public void updateStatistics(final IStatisticsEvent event) {
		String uri = event.getURI();
		InternalStatisticsEntry entry = statistics.get(uri);
		if (entry == null) {
			entry = new InternalStatisticsEntry();
			statistics.putIfAbsent(uri, entry);
		}

		switch (event.getType()) {
		case REQUEST:
			entry.updateRequest();
			break;
		case RESPONSE:
			ResponseEvent responseEvent = (ResponseEvent) event;

			entry.updateResponse(responseEvent.getDuration());
			break;
		}
	}

	public Iterable<StatisticsEntry> exportStatistics() {
		return Iterables.transform(Collections.unmodifiableMap(statistics).entrySet(), new STATISTICS_CONVERTER());
	}
}
