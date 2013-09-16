package com.outgrader.proxy.core.advertisment.rule.impl.internal;

import com.outgrader.proxy.core.advertisment.filter.IFilter;
import com.outgrader.proxy.core.model.IAdvertismentRule;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class CurrentTagRule implements IAdvertismentRule {

	private final String text;

	private final IFilter filter;

	public CurrentTagRule(final String text, final IFilter filter) {
		this.text = text;
		this.filter = filter;
	}

	protected IFilter getFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public boolean matches(final String uri, final ITag tag) {
		return filter.matches(uri, tag);
	}

	@Override
	public boolean isRewritable(final ITag startTag, final ITag currentTag) {
		return true;
	}

}
