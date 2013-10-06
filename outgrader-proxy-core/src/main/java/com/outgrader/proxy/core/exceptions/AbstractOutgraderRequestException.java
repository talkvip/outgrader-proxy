package com.outgrader.proxy.core.exceptions;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class AbstractOutgraderRequestException extends AbstractOutgraderException {

	private static final long serialVersionUID = -8884610378580133650L;

	private final String url;

	protected AbstractOutgraderRequestException(final String url, final String message, final Exception cause) {
		super(message, cause);
		this.url = url;
	}

	protected AbstractOutgraderRequestException(final String url, final String message) {
		super(message);
		this.url = url;
	}

	protected AbstractOutgraderRequestException(final String url, final Exception e) {
		super(e);
		this.url = url;
	}

	protected AbstractOutgraderRequestException(final String url) {
		super();
		this.url = url;
	}

	public String getURL() {
		return url;
	}

}
