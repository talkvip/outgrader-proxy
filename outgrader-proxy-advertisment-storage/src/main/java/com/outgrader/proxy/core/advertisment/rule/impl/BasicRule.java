package com.outgrader.proxy.core.advertisment.rule.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.outgrader.proxy.core.advertisment.rule.impl.internal.AbstractTextRule;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class BasicRule extends AbstractTextRule {

	private static final String[] DELIMETERS = { "//", "/", "?", "=", "&" };

	public static class BasicRuleBuilder {

		private final String line;

		private final List<String> patterns = new ArrayList<>();

		public BasicRuleBuilder(final String line) {
			this.line = line;
		}

		public BasicRuleBuilder shouldEndWith(final String endPattern) {
			return shouldContain(endPattern + "\"");
		}

		public BasicRuleBuilder shouldContain(final String containPattern) {
			int rounds = StringUtils.countMatches(containPattern, "^");

			String pattern = new String(containPattern);

			if (rounds > 0) {
				patterns.add(containPattern.replaceAll("^", StringUtils.EMPTY));
				for (String delimeter : DELIMETERS) {
					shouldContain(pattern.replace("^", delimeter));
				}
			} else {
				patterns.add(containPattern);
			}

			return this;
		}

		public BasicRuleBuilder shouldStartWith(final String startPattern) {
			return shouldContain("//" + startPattern);
		}

		public BasicRule build() {
			return new BasicRule(line, patterns.toArray(new String[patterns.size()]));
		}
	}

	private final String[] patterns;

	private BasicRule(final String text, final String[] patterns) {
		super(text);

		this.patterns = patterns;
	}

	@Override
	protected boolean matches(final String tagText) {
		for (String pattern : patterns) {
			if (!tagText.contains(pattern)) {
				return false;
			}
		}
		return true;
	}

}
