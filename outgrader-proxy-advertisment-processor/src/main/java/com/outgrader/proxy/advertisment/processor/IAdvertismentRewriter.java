package com.outgrader.proxy.advertisment.processor;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

import com.outgrader.proxy.advertisment.processor.internal.ITag;
import com.outgrader.proxy.advertisment.processor.internal.TagReader;
import com.outgrader.proxy.advertisment.rule.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRewriter {

	ByteBuf rewrite(ITag tag, IAdvertismentRule rule, Charset charset, TagReader tagReader);

	ByteBuf rewrite(ITag tag, Charset charset);

}
