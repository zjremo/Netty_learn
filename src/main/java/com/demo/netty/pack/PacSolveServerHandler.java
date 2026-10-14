package com.demo.netty.pack;

import java.nio.charset.Charset;

import cn.hutool.core.lang.UUID;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PacSolveServerHandler extends SimpleChannelInboundHandler<MyPackage>{
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyPackage msg) throws Exception {
        // 解析msg
        int length = msg.getLength();
        byte[] content = msg.getContent();

        System.out.println("Server receive msg length: " + length);
        System.out.println("Server receive msg content: " + new String(content, Charset.forName("utf-8")));
        System.out.println("Server receive msg total count: " + (++this.count));

        // 回传client 一个UUID
        String uuid = UUID.randomUUID(false).toString();
        int respLength = uuid.getBytes("utf-8").length;
        byte[] respContent = uuid.getBytes("utf-8");

        // build package
        MyPackage myPackage = new MyPackage();
        myPackage.setLength(respLength);
        myPackage.setContent(respContent);

        ctx.writeAndFlush(myPackage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("cause message: " + cause.getMessage());
        ctx.close();
    }
}
