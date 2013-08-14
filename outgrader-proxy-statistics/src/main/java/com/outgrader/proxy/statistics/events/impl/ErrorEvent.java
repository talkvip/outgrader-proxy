package com.outgrader.proxy.statistics.events.impl;

import com.outgrader.proxy.statistics.events.StatisticsEventType;
import com.outgrader.proxy.statistics.events.impl.internal.AbstractStatisticsEvent;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public class ErrorEvent extends AbstractStatisticsEvent {

	private final Object source;

	private final String message;

	private final Exception error;

	public ErrorEvent(final String uri, final Object source, final String message, final Exception e) {
		super(StatisticsEventType.ERROR, uri);

		this.source = source;
		this.message = message;
		this.error = e;
	}

	public Object getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}

	public Exception getError() {
		return error;
	}
}
