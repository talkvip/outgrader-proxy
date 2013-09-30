package com.outgrader.proxy.core.exceptions;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.15-SNAPSHOT
 * 
 */
public class AbstractOutgraderException extends Exception {

	private static final long serialVersionUID = -8127253773767630035L;

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
