package com.demo.netty.rpc;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class RequestPackageEncoder extends MessageToByteEncoder<RequestPackage>{

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestPackage msg, ByteBuf out) throws Exception {
        // 1. msg format to json str
        String s = JSONUtil.toJsonStr(msg);

        // 2. write s to buf
        ByteBuf buf = Unpooled.copiedBuffer(s, CharsetUtil.UTF_8);
        int length = buf.readableBytes();

        // 3. write length to out
        out.writeInt(length);

        // 4. wriete buf to out
        out.writeBytes(buf);
    }
    
}
