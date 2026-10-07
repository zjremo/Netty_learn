package com.demo;

import org.junit.Test;

import com.demo.netty.tcpchat.NettyServerHandler;

import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyTest {
    @Test
    public void testNettyServerHandler() {
        NettyServerHandler serverHandler = new NettyServerHandler();
        System.out.println(ChannelInboundHandlerAdapter.class.isInstance(serverHandler));  
    }
}
