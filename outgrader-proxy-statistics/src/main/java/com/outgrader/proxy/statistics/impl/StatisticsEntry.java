package com.outgrader.proxy.statistics.impl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public final class StatisticsEntry implements Comparable<StatisticsEntry> {

	public static class StatisticsEntryBuilder {

		private final StatisticsEntry result;

		public StatisticsEntryBuilder(final String uri, final long periodTimestamp) {
			result = new StatisticsEntry();
			withURI(uri);
			withPeriodTimestamp(periodTimestamp);
		}

		private StatisticsEntryBuilder withPeriodTimestamp(final long periodTimestamp) {
			result.periodTimestamp = periodTimestamp;

			return this;
		}

		private StatisticsEntryBuilder withURI(final String uri) {
			result.uri = uri;

			return this;
		}

		public StatisticsEntryBuilder withErrorCount(final long errorCount) {
			result.errorCount = errorCount;

			return this;
		}

		public StatisticsEntryBuilder withAdvertismentCandidateCount(final long advertismentCandidateCount) {
			result.advertismentCandidateCount = advertismentCandidateCount;

			return this;
		}

		public StatisticsEntryBuilder withRequestCount(final long requestCount) {
			result.requestCount = requestCount;

			return this;
		}

		public StatisticsEntryBuilder withResponseCount(final long responseCount) {
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

	private long advertismentCandidateCount;

	private long errorCount;

	private String uri;

	private long requestCount;

	private long responseCount;

	private long minDuration;

	private long maxDuration;

	private long averageDuration;

	private long periodTimestamp;

	public String getUri() {
		return uri;
	}

	public long getRequestCount() {
		return requestCount;
	}

	public long getResponseCount() {
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

	public long getAdvertismentCandidateCount() {
		return advertismentCandidateCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public long getPeriodTimestamp() {
		return periodTimestamp;
	}

	@Override
	public int compareTo(final StatisticsEntry o) {
		return (int) (o.getAdvertismentCandidateCount() - getAdvertismentCandidateCount());
	}

}
