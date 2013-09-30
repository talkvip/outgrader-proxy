package com.outgrader.proxy.statistics.manager.impl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.12-SNAPSHOT
 * 
 */
public class StatisticsKey {

	private final String url;

	private final long timestamp;

	public StatisticsKey(final String url, final long timestamp) {
		this.url = url;
		this.timestamp = timestamp;
	}

	public String getURL() {
		return url;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (timestamp ^ (timestamp >>> 32));
		result = (prime * result) + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof StatisticsKey) {
			StatisticsKey another = (StatisticsKey) obj;

			return another.url.equals(url) && (another.timestamp == timestamp);
		}

		return super.equals(obj);
	}
}
