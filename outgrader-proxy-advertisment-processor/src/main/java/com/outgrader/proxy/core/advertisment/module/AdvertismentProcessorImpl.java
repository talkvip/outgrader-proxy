package com.outgrader.proxy.core.advertisment.module;

import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Singleton;

import com.outgrader.proxy.core.advertisment.response.IAdvertismentProcessor;
import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Singleton
public class AdvertismentProcessorImpl implements IAdvertismentProcessor {

	@Override
	public ByteBuf process(final InputStream stream, final Charset charset) throws AbstractOutgraderException {

		return null;
	}

}
