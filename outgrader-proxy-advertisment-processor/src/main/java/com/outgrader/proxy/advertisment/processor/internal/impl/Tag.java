package com.outgrader.proxy.advertisment.processor.internal.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.outgrader.proxy.advertisment.processor.internal.impl.utils.TagUtils;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class Tag implements ITag {

	private static final String CLASS_ATTRIBUTE = "class";

	private static final String PATH_SEPARATOR = ">";

	private static final String POINT_SYMBOL = ".";

	public static class TagBuilder {

		private static final String SPACE = " ";

		private static final String TAG_START = "<";

		private static final String OPEN_AND_CLOSE_TAG_END = "/>";

		private static final String TAG_END = PATH_SEPARATOR;

		private static final String CLOSING_TAG_START = "</";

		private final Tag tag;

		private TagBuilder() {
			tag = new Tag();
		}

		public TagBuilder withText(final String text) {
			tag.setText(text);

			int nameStart = -1;
			int nameEnd = -1;

			if (text.startsWith(CLOSING_TAG_START)) {
				tag.setTagType(TagType.CLOSING);

				nameStart = text.indexOf(CLOSING_TAG_START) + 2;
				nameEnd = text.indexOf(TAG_END);
			} else if (text.endsWith(OPEN_AND_CLOSE_TAG_END)) {
				tag.setTagType(TagType.OPEN_AND_CLOSING);

				withOpeningTag(tag);

				nameStart = text.indexOf(TAG_START) + 1;
				nameEnd = text.indexOf(SPACE);
				if (nameEnd < 0) {
					nameEnd = text.indexOf(OPEN_AND_CLOSE_TAG_END);
				}
			} else {
				tag.setTagType(TagType.OPENING);

				nameStart = text.indexOf(TAG_START) + 1;
				nameEnd = text.indexOf(SPACE);
				if (nameEnd < 0) {
					nameEnd = text.indexOf(TAG_END);
				}
			}

			if (tag.isAnalysable()) {
				tag.setName(text.substring(nameStart, nameEnd));
			}

			return this;
		}

		public TagBuilder withOpeningTag(final ITag openingTag) {
			tag.setOpeningTag(openingTag);

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

	private TagType type;

	private ITag openingTag;

	private String name;

	private ITag parent;

	private Map<String, String> attributes = null;

	private String path;

	private String id;

	private String cssId;

	private String cssClass;

	protected Tag() {

	}

	@Override
	public ITag getParent() {
		return parent;
	}

	public void setText(final String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setTagType(final TagType type) {
		this.type = type;
	}

	@Override
	public TagType getTagType() {
		return type;
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

	@Override
	public ITag getOpeningTag() {
		return openingTag;
	}

	@Override
	public void setOpeningTag(final ITag openingTag) {
		this.openingTag = openingTag;
	}

	@Override
	public String getName() {
		if (name == null) {
			name = StringUtils.EMPTY;
		}
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getCSSClass() {
		if (cssClass == null) {
			cssClass = getAttribute(CLASS_ATTRIBUTE);

			if (cssClass == null) {
				cssClass = StringUtils.EMPTY;
			}
		}

		return cssClass;
	}

	@Override
	public String getId() {
		if (id == null) {
			id = getAttribute(ID_ATTRIBUTE);

			if (id == null) {
				id = StringUtils.EMPTY;
			}
		}

		return id;
	}

	@Override
	public String getCSSId() {
		if (cssId == null) {
			StringBuilder builder = new StringBuilder(getName());

			builder.append(POINT_SYMBOL).append(getCSSClass());

			cssId = builder.toString();
		}

		return cssId;
	}

	public void setParent(final ITag tag) {
		this.parent = tag;

		if (parent != null) {
			this.path = new StringBuilder(tag.getPath()).append(PATH_SEPARATOR).append(name).toString();
		} else {
			this.path = name;
		}
	}

	@Override
	public String getAttribute(final String name) {
		if (attributes == null) {
			attributes = TagUtils.getAttributes(getText());
		}

		return attributes.get(name);
	}

	@Override
	public String getPath() {
		if (path == null) {
			return name;
		}

		return path;
	}
}
