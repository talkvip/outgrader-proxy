package com.outgrader.proxy.core.model;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface ITag {

	public static final String ID_ATTRIBUTE = "id";

	public enum TagType {
		OPENING, CLOSING, OPEN_AND_CLOSING;
	}

	void setOpeningTag(ITag tag);

	ITag getParent();

	String getPath();

	String getText();

	TagType getTagType();

	boolean isAnalysable();

	ITag getOpeningTag();

	String getName();

	String getAttribute(String name);

	String getId();

	String getCSSId();

}
