package com.outgrader.proxy.statistics.impl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public final class StatisticsEntry {

	public static class StatisticsEntryBuilder {

		private final StatisticsEntry result;

		public StatisticsEntryBuilder(final String uri) {
			result = new StatisticsEntry();
			withURI(uri);
		}

		private StatisticsEntryBuilder withURI(final String uri) {
			result.uri = uri;

			return this;
		}

		public StatisticsEntryBuilder withRequestCount(final int requestCount) {
			result.requestCount = requestCount;

			return this;
		}

		public StatisticsEntryBuilder withResponseCount(final int responseCount) {
			result.responseCount = responseCount;

			return this;
		}

		public StatisticsEntryBuilder withMinDuration(final long minDuration) {
			result.minDuration = minDuration;

			return this;
		}

		public StatisticsEntryBuilder withMaxDuration(final long maxDuration) {
			result.maxDuration = maxDuration;

			return this;
		}

		public StatisticsEntryBuilder withAverageDuration(final long averageDuration) {
			result.averageDuration = averageDuration;

			return this;
		}

		public StatisticsEntry build() {
			return result;
		}

	}

	private StatisticsEntry() {

	}

	private String uri;

	private int requestCount;

	private int responseCount;

	private long minDuration;

	private long maxDuration;

	private long averageDuration;

	public String getUri() {
		return uri;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public int getResponseCount() {
		return responseCount;
	}

	public long getMinDuration() {
		return minDuration;
	}

	public long getMaxDuration() {
		return maxDuration;
	}

	public long getAverageDuration() {
		return averageDuration;
	}

}
