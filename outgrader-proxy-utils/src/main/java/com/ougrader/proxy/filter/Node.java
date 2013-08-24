package com.ougrader.proxy.filter;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
class Node implements Comparable<Node> {

	private final Collection<Node> children = new TreeSet<>();

	private final char value;

	public Node(final char value) {
		this.value = value;
	}

	public Node() {
		this((char) 0);
	}

	public Collection<Node> getChildren() {
		return children;
	}

	public char getValue() {
		return value;
	}

	@Override
	public int compareTo(final Node o) {
		return value - o.value;
	}

}
