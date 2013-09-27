package com.outgrader.proxy.advertisment.processor.impl;

import java.nio.charset.Charset;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
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
	public byte[] rewrite(final ITag tag, final IAdvertismentRule rule, final Charset charset, final TagReader tagReader) {
		byte[] result = ArrayUtils.EMPTY_BYTE_ARRAY;

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
	public byte[] rewrite(final ITag tag, final Charset charset) {
		return tag.getText().getBytes(charset);
	}

}
