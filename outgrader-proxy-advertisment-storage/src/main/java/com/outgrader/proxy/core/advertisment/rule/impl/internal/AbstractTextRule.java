package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.advertisment.processor.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractTextRule extends AbstractRule {

	protected AbstractTextRule(final String text, final String... patterns) {
		super(text, patterns);
	}

	@Override
	public boolean matches(final ITag tag) {
		return matches(tag.getText());
	}

	protected abstract boolean matches(String tagText);
}
