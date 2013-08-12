package com.outgrader.proxy.core.advertisment.response.internal;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface ITag {

	public enum TagType {
		OPENING, CLOSING, OPEN_AND_CLOSING;
	}

	ITag getParent();

	String getText();

	TagType getTagType();

	boolean isAnalysable();

}
