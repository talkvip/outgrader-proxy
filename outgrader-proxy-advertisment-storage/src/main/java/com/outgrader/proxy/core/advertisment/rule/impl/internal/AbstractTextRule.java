package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public abstract class AbstractTextRule extends AbstractRule {

	protected AbstractTextRule(final String text, final IFilter filter) {
		super(text, filter);
	}

	@Override
	public boolean matches(final String uri, final ITag tag) {
		return matches(tag.getText());
	}

	protected abstract boolean matches(String tagText);
}
