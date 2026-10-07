package com.demo.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.buffer(10);

        for (int i = 0; i < 10; i++) {
            byteBuf.writeByte(i);
        }

        System.out.println("capacity= " + byteBuf.capacity());

        for (int i = 0; i < byteBuf.capacity(); ++i) {
            // read a byte from the buffer
            System.out.println(byteBuf.readByte());
        }
        System.out.println("process over");
    }
}
