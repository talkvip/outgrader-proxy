package com.outgrader.proxy.external.scope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.2.0-SNAPSHOT
 * 
 */
public class ThreadScopeImpl implements Scope {

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {

			@Override
			public T get() {
				ThreadLocalCache cache = ThreadLocalCache.getInstance();
				T value = cache.get(key);

				if (value == null) {
					value = unscoped.get();
					cache.add(key, value);
				}

				return value;
			}

		};
	}
}
