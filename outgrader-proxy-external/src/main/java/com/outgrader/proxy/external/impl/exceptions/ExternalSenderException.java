package com.outgrader.proxy.external.impl.exceptions;

import java.io.IOException;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class ExternalSenderException extends AbstractOutgraderException {

	private static final long serialVersionUID = -2565360584016511583L;

	public ExternalSenderException(final String message, final IOException cause) {
		super(message, cause);
	}

	public ExternalSenderException(final String message) {
		super(message);
	}

}
