package com.outgrader.proxy.statistics.manager.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
class InternalStatisticsEntry {

	private final AtomicInteger requestCount = new AtomicInteger();

	private final AtomicInteger responseCount = new AtomicInteger();

	private AtomicLong minDuration = null;

	private final AtomicLong maxDuration = new AtomicLong();

	private final AtomicLong averageDuration = new AtomicLong();

	private final AtomicLong errorCount = new AtomicLong();

	private final AtomicLong advertismentCandidateCount = new AtomicLong();

	public void updateRequest() {
		requestCount.incrementAndGet();
	}

	public void updateError() {
		errorCount.incrementAndGet();
	}

	public void updateAdvertismentCandidateCount() {
		advertismentCandidateCount.incrementAndGet();
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

	public AtomicLong getErrorCount() {
		return errorCount;
	}

	public AtomicLong getAdvertismentCandidateCount() {
		return advertismentCandidateCount;
	}

}