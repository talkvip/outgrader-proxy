package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.advertisment.rule.IAdvertismentRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractRule implements IAdvertismentRule {

	private final String text;

	protected AbstractRule(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}
