package com.outgrader.proxy.statistics.exceptions;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class StatisticsException extends AbstractOutgraderException {

	private static final long serialVersionUID = -5587659136454590613L;

	public StatisticsException(final String message, final Exception cause) {
		super(message, cause);
	}

}
