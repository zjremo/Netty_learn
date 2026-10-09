package com.demo.netty.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class MyServerHandler extends SimpleChannelInboundHandler<StudentPOJO.Student> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StudentPOJO.Student msg) {
        System.out.println("服务器收到学生信息, id=" + msg.getId() + ", name=" + msg.getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace(System.out);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    // 一段时间没读到客户端数据，认为连接空闲，关闭
                    System.out.println(ctx.channel().remoteAddress() + "--读空闲超时，关闭连接");
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    System.out.println(ctx.channel().remoteAddress() + "--写空闲");
                    break;
                case ALL_IDLE:
                    System.out.println(ctx.channel().remoteAddress() + "--读写空闲");
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
