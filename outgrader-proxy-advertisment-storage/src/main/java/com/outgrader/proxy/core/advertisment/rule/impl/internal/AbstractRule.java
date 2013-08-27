package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.advertisment.rule.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractRule implements IAdvertismentRule {

	private final String text;

	private final String[] patterns;

	protected AbstractRule(final String text, final String... patterns) {
		this.text = text;
		this.patterns = patterns;
	}

	@Override
	public String toString() {
		return text;
	}

	protected String[] getPatterns() {
		return patterns;
	}

}
