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

		private TagBuilder() {
			tag = new Tag();
		}

		public TagBuilder withText(final String text) {
			tag.setText(text);

			return this;
		}

		public static TagBuilder create() {
			return new TagBuilder();
		}

		public static TagBuilder createSpaceTag() {
			TagBuilder result = new TagBuilder();

			result.getTag().setAnalysable(false);

			return result;
		}

		public Tag build() {
			return tag;
		}
		
		private Tag getTag() {
			return tag;
		}

	}

	private String text;

	private boolean isAnalysable = true;

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

	public void setText(final String text) {
		this.text = text;
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

	public void setAnalysable(final boolean isAnalysable) {
		this.isAnalysable = isAnalysable;
	}

	@Override
	public boolean isAnalysable() {
		return isAnalysable;
	}

	@Override
	public String toString() {
		return getText();
	}
}
