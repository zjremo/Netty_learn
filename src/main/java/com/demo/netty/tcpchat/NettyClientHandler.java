package com.demo.netty.tcpchat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * NettyClientHandler
 */
@Slf4j (topic = "NettyClientHandler")
public class NettyClientHandler extends ChannelInboundHandlerAdapter{
    
    /**
     * 当通道就绪就会触发该方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("client " + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, server: Nice to meet you", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.debug("服务器回复的消息: {}", buf.toString(CharsetUtil.UTF_8));
        log.debug("服务器的地址: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("异常信息: {}", cause.getMessage());
        ctx.close(); // 关闭通道
    }
}
