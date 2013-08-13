package com.outgrader.proxy.core.advertisment.response.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.core.advertisment.IAdvertismentRule;
import com.outgrader.proxy.core.advertisment.response.IAdvertismentProcessor;
import com.outgrader.proxy.core.advertisment.response.IAdvertismentRewriter;
import com.outgrader.proxy.core.advertisment.response.internal.ITag;
import com.outgrader.proxy.core.advertisment.response.internal.TagReader;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvertismentProcessorImpl.class);

	private final IAdvertismentRuleStorage ruleStorage;

	private final IStatisticsHandler statisticsHandler;

	private final IAdvertismentRewriter rewriter;

	@Inject
	public AdvertismentProcessorImpl(final IAdvertismentRuleStorage ruleStorage, final IStatisticsHandler statisticsHandler,
			final IAdvertismentRewriter rewriter) {
		this.ruleStorage = ruleStorage;
		this.statisticsHandler = statisticsHandler;
		this.rewriter = rewriter;
	}

	@Override
	public ByteBuf process(final String uri, final InputStream stream, final Charset charset) throws AbstractOutgraderException {
		ByteBuf result = Unpooled.EMPTY_BUFFER;

		Charset actualCharset = charset == null ? Charset.defaultCharset() : charset;

		try (TagReader reader = createTagReader(stream, actualCharset)) {
			for (ITag tag : reader) {
				if (tag.isAnalysable()) {
					boolean isRewritten = false;
					for (IAdvertismentRule rule : ruleStorage.getRules()) {
						if (rule.matches(tag)) {

							statisticsHandler.onAdvertismentCandidateFound(uri, rule.toString());

							isRewritten = true;

							result = Unpooled.copiedBuffer(result, rewriter.rewrite(tag, rule, actualCharset));

							break;
						}
					}

					if (!isRewritten) {
						result = Unpooled.copiedBuffer(result, rewriter.rewrite(tag, actualCharset));
					}
				} else {
					result = Unpooled.copiedBuffer(result, rewriter.rewrite(tag, actualCharset));
				}
			}
		} catch (IOException e) {
			statisticsHandler.onError(this, "An exception occured during processing response", e);

			LOGGER.error("An exception occured during processing response", e);
		}

		return result;
	}

	protected TagReader createTagReader(final InputStream stream, final Charset charset) {
		return new TagReader(stream, charset);
	}
}
