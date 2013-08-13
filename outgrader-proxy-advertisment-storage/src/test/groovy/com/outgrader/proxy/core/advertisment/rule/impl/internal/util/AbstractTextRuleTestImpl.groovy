package com.outgrader.proxy.core.advertisment.rule.impl.internal.util

import java.util.regex.Pattern

import com.outgrader.proxy.core.advertisment.rule.impl.internal.AbstractTextRule

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 *
 */
class AbstractTextRuleTestImpl extends AbstractTextRule {

	public AbstractTextRuleTestImpl(String text, Pattern pattern) {
		super(text, pattern)
	}

	@Override
	protected boolean matches(String tagText) {
		return false
	}
}
