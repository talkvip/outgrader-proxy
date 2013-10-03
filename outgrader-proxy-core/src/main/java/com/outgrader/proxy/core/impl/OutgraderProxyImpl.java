package com.outgrader.proxy.core.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.outgrader.proxy.core.IOutgraderProxy;
import com.outgrader.proxy.core.initializer.IOutgraderChannelInitializer;
import com.outgrader.proxy.core.properties.IOutgraderProperties;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.1.0-SNAPSHOT
 * 
 */
@Component
public class OutgraderProxyImpl implements IOutgraderProxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutgraderProxyImpl.class);

	@Inject
	private IOutgraderProperties properties;

	@Inject
	private IOutgraderChannelInitializer channelInitializer;

	private EventLoopGroup bossGroup;

	private EventLoopGroup workerGroup;

	@Override
	public void run() {
		LOGGER.info("Starting netty.io server");

		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup(properties.getWorkerThreadNumber());

		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
			server.childHandler(channelInitializer);

			Channel channel = server.bind(properties.getPort()).sync().channel();

			LOGGER.info("Outgrader started at port <" + properties.getPort() + ">");

			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			LOGGER.error("An exception occured during Proxy work", e);
		} finally {
			stop();
		}
	}

	@Override
	public void stop() {
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
		}
	}
}
