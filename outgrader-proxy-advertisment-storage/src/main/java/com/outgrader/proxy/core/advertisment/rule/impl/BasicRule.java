package com.outgrader.proxy.core.advertisment.rule.impl;

import com.outgrader.proxy.core.advertisment.rule.impl.internal.AbstractTextRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class BasicRule extends AbstractTextRule {

	public BasicRule(final String text, final String... patterns) {
		super(text, patterns);
	}

	@Override
	protected boolean matches(final String tagText) {
		for (String pattern : getPatterns()) {
			if (!tagText.contains(pattern)) {
				return false;
			}
		}
		return true;
	}

}
