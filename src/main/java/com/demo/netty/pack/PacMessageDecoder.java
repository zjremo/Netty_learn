package com.demo.netty.pack;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
// import io.netty.handler.codec.ByteToMessageDecoder;

// ReplayingDecoder 是ByteToMessageDecoder的子类，内部封装了检查字节长度转换为对象的逻辑
// 不再需要像ByteToMessageDecoder那样手动检查字节长度
public class PacMessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readInt();

        byte[] content = new byte[length];
        in.readBytes(content);

        // 打包消息 封装为MyPackage对象
        MyPackage myPackage = new MyPackage();
        myPackage.setContent(content);
        myPackage.setLength(length);

        out.add(myPackage);
    }
}

/* public class PacMessageDecoder extends ByteToMessageDecoder {

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
    
} */
