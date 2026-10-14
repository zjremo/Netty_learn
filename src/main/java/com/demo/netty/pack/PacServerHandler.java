package com.demo.netty.pack;

import cn.hutool.core.lang.UUID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class PacServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // 读取数据并打印，回复客户端一个UUID
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);

        // 将buffer转为字符串 
        String message = new String(buffer, CharsetUtil.UTF_8);
        System.out.println("message from client: " + message);
        System.out.println("message length: " + buffer.length);

        // 回复客户端一个UUID
        String uuid = UUID.randomUUID(false).toString();
        ByteBuf response = Unpooled.copiedBuffer(uuid, CharsetUtil.UTF_8);
        ctx.writeAndFlush(response);
    }
}
