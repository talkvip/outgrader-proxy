package com.outgrader.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.outgrader.proxy.core.IOutgraderProxy;

/**
 * Main entry point to application
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0
 */
public final class Outgrader {

	private static final Logger LOGGER = LoggerFactory.getLogger(Outgrader.class);

	private static final String CONTEXT_LOCATION = "classpath*:META-INF/*/applicationContext.xml";

	private ApplicationContext context;

	private Outgrader() {

	}

	/**
	 * For tests only
	 */
	protected Outgrader(final ApplicationContext context) {
		this.context = context;
	}

	public void run() {
		LOGGER.info("Creating instance of Outgrader Proxy and start it");
		IOutgraderProxy proxy = getApplicationContext().getBean(IOutgraderProxy.class);
		proxy.run();
	}

	protected ApplicationContext getApplicationContext() {
		LOGGER.info("Initializing Spring environment");

		if (context == null) {
			context = new ClassPathXmlApplicationContext(CONTEXT_LOCATION);
		}

		return context;
	}

	public static void main(final String[] args) {
		LOGGER.info("Starting Ougrader-Runner application");

		new Outgrader().run();

		LOGGER.info("Closing Outgrader-Runner application");
	}
}
