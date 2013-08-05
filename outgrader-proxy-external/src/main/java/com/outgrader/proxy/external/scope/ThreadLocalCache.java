package com.outgrader.proxy.external.scope;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public final class ThreadLocalCache {

	private static final ThreadLocal<ThreadLocalCache> THREAD_LOCAL = new ThreadLocal<ThreadLocalCache>() {
		@Override
		protected ThreadLocalCache initialValue() {
			return new ThreadLocalCache();
		}
	};

	private final Map<Key<?>, Object> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T get(final Key<T> key) {
		return (T) map.get(key);
	}

	public <T> void add(final Key<T> key, final T value) {
		map.put(key, value);
	}

	public static ThreadLocalCache getInstance() {
		return THREAD_LOCAL.get();
	}

}
