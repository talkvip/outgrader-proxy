package com.outgrader.proxy.core.advertisment.response.internal.impl;

import com.outgrader.proxy.core.advertisment.response.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class Tag implements ITag {

	public static class TagBuilder {

		private final Tag tag;

		private TagBuilder(final String text) {
			tag = new Tag();
			tag.text = text;
		}

		public static TagBuilder withText(final String text) {
			return new TagBuilder(text);
		}

		public Tag build() {
			return tag;
		}

	}

	private String text;

	protected Tag() {

	}

	protected Tag(final String text) {
		this.text = text;
	}

	@Override
	public ITag getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public TagType getTagType() {
		// TODO Auto-generated method stub
		return null;
	}

}
