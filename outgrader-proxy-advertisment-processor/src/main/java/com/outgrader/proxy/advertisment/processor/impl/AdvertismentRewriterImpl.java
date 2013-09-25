package com.outgrader.proxy.advertisment.processor.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter;
import com.outgrader.proxy.advertisment.processor.internal.TagReader;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.model.ITag;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.properties.IOutgraderProperties.RewriteMode;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Component
public class AdvertismentRewriterImpl implements IAdvertismentRewriter {

	private final RewriteMode rewriteMode;

	@Inject
	public AdvertismentRewriterImpl(final IOutgraderProperties properties) {
		this.rewriteMode = properties.getRewriteMode();
	}

	@Override
	public ByteBuf rewrite(final ITag tag, final IAdvertismentRule rule, final Charset charset, final TagReader tagReader) {
		ByteBuf result = Unpooled.EMPTY_BUFFER;

		if (rewriteMode == RewriteMode.ON) {
			ITag nextTag = tag;

			while (tagReader.hasNext() && rule.isRuleRewriteContinues(tag, nextTag)) {
				nextTag = tagReader.next();
			}
		} else {
			result = rewrite(tag, charset);
		}

		return result;
	}

	@Override
	public ByteBuf rewrite(final ITag tag, final Charset charset) {
		return Unpooled.wrappedBuffer(tag.getText().getBytes(charset));
	}

}
