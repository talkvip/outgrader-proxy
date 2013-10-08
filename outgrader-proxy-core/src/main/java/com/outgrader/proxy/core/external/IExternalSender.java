package com.outgrader.proxy.core.external;

import com.outgrader.proxy.core.exceptions.AbstractOutgraderRequestException;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
public interface IExternalSender {

	HttpResponse send(String hostName, HttpRequest request) throws AbstractOutgraderRequestException;

}
