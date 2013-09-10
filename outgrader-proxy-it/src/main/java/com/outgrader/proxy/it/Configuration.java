package com.outgrader.proxy.it;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public final class Configuration {

	private static Configuration instance = null;

	private Configuration() {

	}

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}

		return instance;
	}

	public Set<String> getURLList() {
		return Sets.newHashSet("http://www.tut.by", "http://www.onliner.by", "http://www.google.by");
	}

}
