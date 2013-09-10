package com.outgrader.proxy.it;

import java.io.IOException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public interface ISender {

	String send(String url) throws IOException;

	String sendProxy(String url, int proxyPort) throws IOException;

}
