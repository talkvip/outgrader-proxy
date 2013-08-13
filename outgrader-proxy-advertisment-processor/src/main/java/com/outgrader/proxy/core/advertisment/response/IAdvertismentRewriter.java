package com.outgrader.proxy.core.advertisment.response;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

import com.outgrader.proxy.core.advertisment.IAdvertismentRule;
import com.outgrader.proxy.core.advertisment.response.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRewriter {

	ByteBuf rewrite(ITag tag, IAdvertismentRule rule, Charset charset);

	ByteBuf rewrite(ITag tag, Charset charset);

}
