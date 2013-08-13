package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import java.util.regex.Pattern;

import com.outgrader.proxy.advertisment.rule.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractRule implements IAdvertismentRule {

	private final String text;

	private final Pattern pattern;

	protected AbstractRule(final String text, final Pattern pattern) {
		this.text = text;
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		return text;
	}

	protected Pattern getPattern() {
		return pattern;
	}

}
