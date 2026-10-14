package com.demo.netty.pack;

import java.nio.charset.Charset;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PacSolveClientHandler extends SimpleChannelInboundHandler<MyPackage> {
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 使用客户端发送10条 Hello Server
        String msg = "Hello Server";

        for (int i = 0; i < 5; ++i) {
            byte[] content = msg.getBytes(Charset.forName("utf-8"));
            int length = content.length;

            MyPackage myPackage = new MyPackage();
            myPackage.setContent(content);
            myPackage.setLength(length);
            ctx.writeAndFlush(myPackage);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyPackage msg) throws Exception {
        int length = msg.getLength();
        byte[] content = msg.getContent();
        
        System.out.println("client receive msg length: " + length);
        System.out.println("client receive msg content: " + new String(content, Charset.forName("utf-8")));
        System.out.println("client receive msg count: " + (++this.count));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("cause message is " + cause.getMessage());
        ctx.close();
    }
}
