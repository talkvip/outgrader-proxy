package com.outgrager.proxy.core.impl;

import javax.inject.Inject;

import com.outgrager.proxy.core.IOutgraderProxy;
import com.outgrager.proxy.core.properties.IOutgraderProperties;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class OutgraderProxyImpl implements IOutgraderProxy {

	@Inject
	private IOutgraderProperties properties;

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

}
