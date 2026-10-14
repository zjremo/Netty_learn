package com.demo.netty.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 每次入站从ByteBuf中读取4个字节，转换为int，并添加到out列表中
        if (in.readableBytes() >= 4) {
            out.add(in.readInt());
        }
    }
}
