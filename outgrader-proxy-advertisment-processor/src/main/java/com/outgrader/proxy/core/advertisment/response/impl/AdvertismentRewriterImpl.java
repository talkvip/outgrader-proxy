package com.outgrader.proxy.core.advertisment.response.impl;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

import com.outgrader.proxy.core.advertisment.IAdvertismentRule;
import com.outgrader.proxy.core.advertisment.response.IAdvertismentRewriter;
import com.outgrader.proxy.core.advertisment.response.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class AdvertismentRewriterImpl implements IAdvertismentRewriter {

	@Override
	public ByteBuf rewrite(final ITag tag, final IAdvertismentRule rule, final Charset charset) {
		return null;
	}

	@Override
	public ByteBuf rewrite(final ITag tag, final Charset charset) {
		// TODO Auto-generated method stub
		return null;
	}

}
