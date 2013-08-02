package com.outgrager.proxy.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrager.proxy.core.IOutgraderProxy;
import com.outgrager.proxy.core.properties.IOutgraderProperties;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderProxyImpl implements IOutgraderProxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderProxyImpl.class);

	@Inject
	private IOutgraderProperties properties;

	@Override
	public void start() {
		LOGGER.info("Starting netty.io server");

		for (int port : properties.getPorts()) {

		}
	}

	private void validate() {
		checkNotNull(properties.getPorts(), "Array of supported post cannot be null");
		// TODO: LN, 1.08.2013, check not empty array
	}

}
