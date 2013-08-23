package com.ougrader.proxy.filter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public class StringFilter {

	private final Node root;

	private StringFilter() {
		root = new Node();
	}

	public static StringFilter createStringFilter() {
		return new StringFilter();
	}

	public boolean matches(final String exression) {

		return false;
	}

	public void addCondition(final String condition) {

	}

}
