package com.demo.netty.buf;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf02 {
    public static void main(String[] args) {
        // netty 自己维护的bytebuffer，不需要flip()，内部三区域：readerIndex, writerIndex, capacity
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello, world!", Charset.forName("utf-8"));

        if (byteBuf.hasArray()) {
            byte[] content = byteBuf.array();

            System.out.println(new String(content, Charset.forName("utf-8")));
            System.out.println("byteBuf= " + byteBuf);

            System.out.println(byteBuf.arrayOffset());
            System.out.println(byteBuf.readerIndex());
            System.out.println(byteBuf.writerIndex());
            System.out.println(byteBuf.capacity());

            System.out.println(byteBuf.getByte(0));

            int len = byteBuf.readableBytes();
            System.out.println("len=" + len);

            // for read all bytes
            for (int i = 0; i < len; i++) {
                System.out.println((char) byteBuf.getByte(i));
            }

            System.out.println(byteBuf.getCharSequence(0, 4, Charset.forName("utf-8")));
            System.out.println(byteBuf.getCharSequence(4, 6, Charset.forName("utf-8")));
        }
    }
}
