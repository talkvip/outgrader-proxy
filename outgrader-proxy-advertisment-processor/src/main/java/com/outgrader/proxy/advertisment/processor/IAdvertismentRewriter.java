package com.outgrader.proxy.advertisment.processor;

import java.nio.charset.Charset;

import com.outgrader.proxy.advertisment.processor.internal.TagReader;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRewriter {

	byte[] rewrite(ITag tag, IAdvertismentRule rule, Charset charset, TagReader tagReader);

	byte[] rewrite(ITag tag, Charset charset);

}
