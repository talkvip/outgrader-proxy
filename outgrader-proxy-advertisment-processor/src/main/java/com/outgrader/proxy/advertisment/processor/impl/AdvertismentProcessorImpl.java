package com.outgrader.proxy.advertisment.processor.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.advertisment.processor.IAdvertismentRewriter;
import com.outgrader.proxy.advertisment.processor.internal.TagReader;
import com.outgrader.proxy.advertisment.processor.internal.impl.utils.ByteArrayBuilder;
import com.outgrader.proxy.core.advertisment.processor.IAdvertismentProcessor;
import com.outgrader.proxy.core.exceptions.AbstractOutgraderRequestException;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.model.ITag;
import com.outgrader.proxy.core.model.ITag.TagType;
import com.outgrader.proxy.core.properties.IOutgraderProperties;
import com.outgrader.proxy.core.statistics.IStatisticsHandler;
import com.outgrader.proxy.core.storage.IAdvertismentRuleStorage;
import com.outgrader.proxy.core.storage.IAdvertismentRuleVault;

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

	@Override
	public ByteBuf process(final String uri, final InputStream stream, final Charset charset) throws AbstractOutgraderRequestException {
		ByteArrayBuilder result = new ByteArrayBuilder();

		IAdvertismentRuleVault mainVault = ruleStorage.getIncludingRulesVault();
		IAdvertismentRuleVault urlVault = mainVault.getSubVault(uri.toLowerCase());
		mainVault = urlVault == null ? mainVault : urlVault;

		try (TagReader reader = createTagReader(stream, charset)) {
			for (ITag tag : reader) {
				if (isAnalysable(tag)) {
					boolean isRewritten = false;

					IAdvertismentRuleVault tagVault = mainVault.getSubVault(tag.getName().toLowerCase());
					IAdvertismentRuleVault currentVault = tagVault == null ? mainVault : tagVault;

					for (IAdvertismentRule includingRule : currentVault.getRules()) {
						if (((includingRule.getSubRules().length > 0) && (currentVault == tagVault)) || includingRule.matches(uri, tag)) {
							boolean stillIncluding = true;

							ITag advertismentTag = tag;

							int subRuleLength = includingRule.getSubRules().length;
							int lastSubRuleIndex = subRuleLength - 1;

							if (subRuleLength > 0) {
								append(result, tag, charset);
								isRewritten = true;
							}

							for (int i = 0; (i < subRuleLength) && reader.hasNext(); i++) {
								IAdvertismentRule subRule = includingRule.getSubRules()[i];
								advertismentTag = reader.next();

								boolean matches = subRule.matches(uri, advertismentTag);
								boolean last = i == lastSubRuleIndex;

								if (!last || (last && !matches)) {
									append(result, advertismentTag, charset);
								}

								if (!matches) {
									stillIncluding = false;
									break;
								}
							}

							for (IAdvertismentRule excludingRule : ruleStorage.getExcludingRules()) {
								if (excludingRule.matches(uri, advertismentTag)) {
									stillIncluding = false;
									break;
								}
							}

							if (stillIncluding) {
								statisticsHandler.onAdvertismentCandidateFound(uri, includingRule.toString());

								isRewritten = true;

								append(result, advertismentTag, includingRule, charset, reader);

								break;
							}
						}
					}

					if (!isRewritten) {
						append(result, tag, charset);
					}
				} else {
					append(result, tag, charset);
				}
			}
		} catch (IOException e) {
			statisticsHandler.onError(uri, this, "An exception occured during processing response", e);

			LOGGER.error("An exception occured during processing response", e);
		}

		return Unpooled.wrappedBuffer(result.build());
	}

	private void append(final ByteArrayBuilder builder, final ITag tag, final IAdvertismentRule rule, final Charset charset,
			final TagReader tagReader) {
		byte[] tagBuffer = rewriter.rewrite(tag, rule, charset, tagReader);

		builder.append(tagBuffer);
	}

	private void append(final ByteArrayBuilder builder, final ITag tag, final Charset charset) {
		byte[] tagBuffer = rewriter.rewrite(tag, charset);

		builder.append(tagBuffer);
	}

	protected TagReader createTagReader(final InputStream stream, final Charset charset) {
		return new TagReader(stream, charset);
	}

	protected boolean isAnalysable(final ITag tag) {
		return tag.isAnalysable() && (tag.getTagType() != TagType.CLOSING) && properties.getSupportedTags().contains(tag.getName());
	}
}
