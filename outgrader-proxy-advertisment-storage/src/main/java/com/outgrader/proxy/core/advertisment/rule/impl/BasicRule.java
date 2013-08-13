package com.outgrader.proxy.core.advertisment.rule.impl;

import java.util.regex.Pattern;

import com.outgrader.proxy.core.advertisment.rule.impl.internal.AbstractTextRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class BasicRule extends AbstractTextRule {

	public BasicRule(final String text, final Pattern pattern) {
		super(text, pattern);
	}

	@Override
	protected boolean matches(final String tagText) {
		return getPattern().matcher(tagText).matches();
	}

}
