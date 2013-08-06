package com.outgrader.proxy.statistics.exceptions;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class StatisticsExportException extends AbstractOutgraderException {

	private static final long serialVersionUID = -6249880712408841129L;

	public StatisticsExportException(final String message, final Exception cause) {
		super(message, cause);
	}

	StatisticsExportException() {
		super();
	}

}
