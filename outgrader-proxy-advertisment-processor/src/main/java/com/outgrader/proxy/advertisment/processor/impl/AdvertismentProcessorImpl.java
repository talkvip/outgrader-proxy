package com.outgrader.proxy.advertisment.processor.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;

import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter;
import com.outgrader.proxy.advertisment.processor.internal.TagReader;
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor;
import com.outgrader.proxy.core.exceptions.AbstractOutgraderException;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.model.ITag;
import com.outgrader.proxy.core.model.ITag.TagType;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
@Component
public class AdvertismentProcessorImpl implements IAdvertismentProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvertismentProcessorImpl.class);

	private final IAdvertismentRuleStorage ruleStorage;

	private final IStatisticsHandler statisticsHandler;

	private final IAdvertismentRewriter rewriter;

	private final IOutgraderProperties properties;

	@Inject
	public AdvertismentProcessorImpl(final IAdvertismentRuleStorage ruleStorage, final IStatisticsHandler statisticsHandler,
			final IAdvertismentRewriter rewriter, final IOutgraderProperties properties) {
		this.ruleStorage = ruleStorage;
		this.statisticsHandler = statisticsHandler;
		this.rewriter = rewriter;

		this.properties = properties;
	}

	@Profiled
	@Override
	public ByteBuf process(final String uri, final InputStream stream, final Charset charset) throws AbstractOutgraderException {
		ByteBuf result = Unpooled.EMPTY_BUFFER;

		try (TagReader reader = createTagReader(stream, charset)) {
			for (ITag tag : reader) {
				if (isAnalysable(tag)) {
					boolean isRewritten = false;

					for (IAdvertismentRule includingRule : ruleStorage.getIncludingRules()) {
						if (includingRule.isRuleStarted(uri, tag)) {
							boolean stillIncluding = true;

							for (IAdvertismentRule excludingRule : ruleStorage.getExcludingRules()) {
								if (excludingRule.isRuleStarted(uri, tag)) {
									stillIncluding = false;
									break;
								}
							}

							if (stillIncluding) {
								statisticsHandler.onAdvertismentCandidateFound(uri, includingRule.toString());

								isRewritten = true;

								result = Unpooled.copiedBuffer(result, rewriter.rewrite(tag, includingRule, charset, reader));

								break;
							}
						}
					}

					if (!isRewritten) {
						result = Unpooled.copiedBuffer(result, rewriter.rewrite(tag, charset));
					}
				} else {
					result = Unpooled.copiedBuffer(result, rewriter.rewrite(tag, charset));
				}
			}
		} catch (IOException e) {
			statisticsHandler.onError(uri, this, "An exception occured during processing response", e);

			LOGGER.error("An exception occured during processing response", e);
		}

		return result;
	}

	protected TagReader createTagReader(final InputStream stream, final Charset charset) {
		return new TagReader(stream, charset);
	}

	protected boolean isAnalysable(final ITag tag) {
		return tag.isAnalysable() && (tag.getTagType() != TagType.CLOSING) && properties.getSupportedTags().contains(tag.getName());
	}
}
