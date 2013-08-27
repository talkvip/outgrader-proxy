package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.advertisment.processor.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractTextRule extends AbstractRule {

	private static final String[] SUPPORTED_ATTRIBUTES = { "src", "href" };

	protected AbstractTextRule(final String text, final String... patterns) {
		super(text, patterns);
	}

	@Override
	public boolean matches(final ITag tag) {
		// check if tag have attribute, if no attribute - no sense to check
		// this tag
		if (tag.haveAttribute(SUPPORTED_ATTRIBUTES)) {
			return matches(tag.getText());
		}

		return false;
	}

	protected abstract boolean matches(String tagText);
}
