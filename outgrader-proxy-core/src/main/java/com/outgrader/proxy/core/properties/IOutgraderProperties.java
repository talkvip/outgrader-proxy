package com.outgrader.proxy.core.properties;

import java.util.Set;

/**
 * Describes configuration of Proxy
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public interface IOutgraderProperties {

	/**
	 * Array of Ports a Proxy will be bind to
	 */
	int getPort();

	/**
	 * Number of Boss Threads for netty.io
	 */
	int getBossThreadNumber();

	/**
	 * Number of Worker Threads for netty.io
	 */
	int getWorkerThreadNumber();

	/**
	 * Number of threads to handle statistics
	 */
	int getStatisticsThreadNumber();

	/**
	 * Period (in minutes) to run statistics export
	 */
	int getStatisticsExportPeriod();

	/**
	 * Folder to store statistics
	 */
	String getStatisticsExportDirectory();

	/**
	 * Location of initial file with advertisment rules
	 */
	String getAdvertismentListLocation();

	/**
	 * Returns list of Tag types that will be analyzed for Advertisment
	 * 
	 */
	Set<String> getSupportedTags();
}
