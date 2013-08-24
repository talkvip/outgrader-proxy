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

	public boolean matches(final String expression) {
		Node entryPoint = null;
		int index = -1;

		for (Node child : root.getChildren()) {
			index = expression.indexOf(child.getValue());

			if (index >= 0) {
				entryPoint = child;
				break;
			}
		}

		if ((entryPoint != null) && (index >= 0)) {
			return matches(entryPoint, index, expression);
		}

		return false;
	}

	private boolean matches(final Node entryPoint, final int startIndex, final String expression) {
		Node currentNode = entryPoint;
		for (int i = startIndex + 1; i < expression.length(); i++) {
			char currentChar = expression.charAt(i);

			boolean matches = false;
			for (Node child : currentNode.getChildren()) {
				if (child.getValue() == currentChar) {
					matches = true;
					currentNode = child;
					break;
				}
			}

			if (!matches) {
				return false;
			}

			if (currentNode.getChildren().isEmpty()) {
				break;
			}
		}

		return true;
	}

	public void addCondition(final String condition) {
		Node currentNode = root;

		for (char character : condition.toCharArray()) {
			boolean added = false;
			for (Node child : currentNode.getChildren()) {
				if (child.getValue() == character) {
					currentNode = child;
					break;
				}
			}

			if (!added) {
				Node newNode = new Node(character);
				currentNode.getChildren().add(newNode);

				currentNode = newNode;
			}
		}
	}
}
