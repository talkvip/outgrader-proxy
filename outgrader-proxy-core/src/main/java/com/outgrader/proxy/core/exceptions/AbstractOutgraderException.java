package com.outgrader.proxy.core.exceptions;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class AbstractOutgraderException extends Exception {

	private static final long serialVersionUID = -8884610378580133650L;

	protected AbstractOutgraderException(final String message, final Exception cause) {
		super(message, cause);
	}

	protected AbstractOutgraderException(final String message) {
		super(message);
	}

	protected AbstractOutgraderException() {
		super();
	}

}
