package com.outgrader.proxy.advertisment.processor.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import org.springframework.stereotype.Component;

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter;
import com.outgrader.proxy.advertisment.processor.internal.TagReader;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.model.ITag;
import com.outgrader.proxy.core.model.ITag.TagType;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Component
public class AdvertismentRewriterImpl implements IAdvertismentRewriter {

	@Override
	public ByteBuf rewrite(final ITag tag, final IAdvertismentRule rule, final Charset charset, final TagReader tagReader) {
		if (tag.getTagType() != TagType.OPEN_AND_CLOSING) {
			ITag nextTag = null;
			do {
				nextTag = tagReader.next();
			} while ((nextTag != null) && ((nextTag.getOpeningTag() == null) || !nextTag.getOpeningTag().equals(tag)));
		}

		return Unpooled.EMPTY_BUFFER;
	}

	@Override
	public ByteBuf rewrite(final ITag tag, final Charset charset) {
		return Unpooled.wrappedBuffer(tag.getText().getBytes(charset));
	}

}
