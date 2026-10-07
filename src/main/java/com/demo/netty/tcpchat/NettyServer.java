package com.demo.netty.tcpchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j (topic = "NettyServer")
public class NettyServer {
    public static void main(String[] args) {
        // 1. create two netty group: bossGroup and workerGroup
        // 1.1 bossGroup: only accept connection request
        // 1.2 workerGroup: handle the request and business logic
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
        
        log.debug("netty server is ready...");

        try {
            ChannelFuture cf = serverBootstrap.bind(6668).sync();
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.debug("bind port 6668 success");
                    } else {
                        log.debug("bind port 6668 failed");
                    }
                }
            });
            // 上面的bind会创建出来这条服务端channel，其实就是new出来的NioServerSocketChannel对象。
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
