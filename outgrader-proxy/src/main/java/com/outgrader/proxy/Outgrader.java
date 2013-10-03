package com.outgrader.proxy;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.outgrader.proxy.core.IOutgraderProxy;

/**
 * Main entry point to application
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0
 */
public final class Outgrader implements Daemon {

	private static final Logger LOGGER = LoggerFactory.getLogger(Outgrader.class);

	private static final String CONTEXT_LOCATION = "classpath*:META-INF/*/applicationContext.xml";

	private ApplicationContext context;

	public Outgrader() {

	}

	/**
	 * For tests only
	 */
	protected Outgrader(final ApplicationContext context) {
		this.context = context;
	}

	public void run() {
		LOGGER.info("Creating instance of Outgrader Proxy and start it");
		IOutgraderProxy proxy = getOutgraderProxy();
		proxy.run();
	}

	private IOutgraderProxy getOutgraderProxy() {
		return getApplicationContext().getBean(IOutgraderProxy.class);
	}

	protected ApplicationContext getApplicationContext() {
		LOGGER.info("Initializing Spring environment");

		if (context == null) {
			context = new ClassPathXmlApplicationContext(CONTEXT_LOCATION);
		}

		return context;
	}

	@Override
	public void init(final DaemonContext context) throws DaemonInitException, Exception {
		getApplicationContext();
	}

	@Override
	public void start() throws Exception {
		getOutgraderProxy().run();
	}

	@Override
	public void stop() throws Exception {
		getOutgraderProxy().stop();
	}

	@Override
	public void destroy() {
		((AbstractApplicationContext) getApplicationContext()).close();
	}

	public static void main(final String[] args) {
		LOGGER.info("Starting Ougrader-Runner application");

		new Outgrader().run();

		LOGGER.info("Closing Outgrader-Runner application");
	}

}
