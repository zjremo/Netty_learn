package com.demo.netty.pack;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("call PacMessageDecoder method");
        // decode bytebuf into mypackage
        // 1. read length（半包时要回到标记点，等下次凑齐再读）
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();

        // 2. read content
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        byte[] content = new byte[length];
        in.readBytes(content);

        MyPackage myPackage = new MyPackage();
        myPackage.setContent(content);
        myPackage.setLength(length);

        out.add(myPackage);
    }
    
}
