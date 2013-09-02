package com.outgrader.proxy.core.model;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface ITag {

	public enum TagType {
		OPENING, CLOSING, OPEN_AND_CLOSING;
	}

	void setParent(ITag tag);

	void setOpeningTag(ITag tag);

	ITag getParent();

	String getText();

	TagType getTagType();

	boolean isAnalysable();

	ITag getOpeningTag();

	String getName();

}
