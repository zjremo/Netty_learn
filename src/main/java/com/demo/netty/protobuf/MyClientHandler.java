package com.demo.netty.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        StudentPOJO.Student student = StudentPOJO.Student.newBuilder()
                .setId(1)
                .setName("zhangsan")
                .build();
        System.out.println("客户端发送学生信息, id=" + student.getId() + ", name=" + student.getName());
        ctx.writeAndFlush(student);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace(System.out);
        ctx.close();
    }
}
