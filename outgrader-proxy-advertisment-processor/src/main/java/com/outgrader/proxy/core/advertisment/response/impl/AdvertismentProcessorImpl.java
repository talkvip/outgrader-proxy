package com.outgrader.proxy.core.advertisment.response.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.outgrader.proxy.core.advertisment.response.IAdvertismentProcessor;
import com.outgrader.proxy.core.advertisment.storage.IAdvertismentRuleStorage;
import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Singleton
public class AdvertismentProcessorImpl implements IAdvertismentProcessor {

	@Inject
	IAdvertismentRuleStorage ruleStorage;

	@Inject
	IStatisticsHandler statisticsHandler;

	@Override
	public ByteBuf process(String uri, final InputStream stream, final Charset charset) throws AbstractOutgraderException {
		ByteBuf result = Unpooled.EMPTY_BUFFER;

		return result;
	}

}
