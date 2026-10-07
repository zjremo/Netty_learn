package com.demo.netty.tcpchat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * NettyServerHandler
 */
@Slf4j (topic = "NettyServerHandler")
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("服务器读取线程: {}, channel: {}", Thread.currentThread().getName(), ctx.channel());
        log.debug("server ctx = {}", ctx);
        log.debug("看看channel 和 pipeline 的关系");
        Channel channel = ctx.channel();

        //将msg转成一个ByteBuf
        ByteBuf buf = (ByteBuf) msg;
        log.debug("客户端发送的消息是: {}", buf.toString(CharsetUtil.UTF_8));
        log.debug("客户端地址是: {}", channel.remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, client. Nice to meet you!", CharsetUtil.UTF_8));
    }

    @Override 
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("异常信息: {}", cause.getMessage());
        ctx.close();
    }
}