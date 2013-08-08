package com.outgrader.proxy.core.statistics.advertisment.response;

import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.nio.charset.Charset;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentProcessor {

	public ByteBuf process(InputStream stream, Charset charset) throws AbstractOutgraderException;

}
