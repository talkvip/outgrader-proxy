package com.outgrader.proxy.external.module;

import com.google.inject.AbstractModule;
import com.outgrader.proxy.core.external.IExternalSender;
import com.outgrader.proxy.external.impl.ExternalSenderImpl;
import com.outgrader.proxy.external.scope.ThreadScope;
import com.outgrader.proxy.external.scope.ThreadScopeImpl;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public class ExternalSenderModule extends AbstractModule {

	@Override
	protected void configure() {
		bindScope(ThreadScope.class, new ThreadScopeImpl());

		bind(IExternalSender.class).to(ExternalSenderImpl.class);
	}
}
