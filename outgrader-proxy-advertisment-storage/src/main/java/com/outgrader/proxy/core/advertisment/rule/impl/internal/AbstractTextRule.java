package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import java.util.regex.Pattern;

import com.outgrader.proxy.core.advertisment.response.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractTextRule extends AbstractRule {

	protected AbstractTextRule(final String text, final Pattern pattern) {
		super(text, pattern);
	}

	@Override
	public boolean matches(final ITag tag) {
		return matches(tag.getText());
	}

	protected abstract boolean matches(String tagText);
}
