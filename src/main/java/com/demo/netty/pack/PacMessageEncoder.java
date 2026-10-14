package com.demo.netty.pack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacMessageEncoder extends MessageToByteEncoder<MyPackage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MyPackage msg, ByteBuf out) throws Exception {
        System.out.println("call PacMessageEncoder method");
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getContent());
    }
}