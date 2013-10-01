package com.outgrader.proxy.advertisment.processor.exceptions;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderRequestException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.16-SNAPSHOT
 * 
 */
public class AdvertismentProcessorException extends AbstractOutgraderRequestException {

	private static final long serialVersionUID = -7794510144034064978L;

	public AdvertismentProcessorException(final String url, final String message, final Exception cause) {
		super(url, message, cause);
	}

}
