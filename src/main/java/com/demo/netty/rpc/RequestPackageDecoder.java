package com.demo.netty.rpc;

import java.util.List;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;

public class RequestPackageDecoder extends ReplayingDecoder<Void>{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. get length from in
        int length = in.readInt();

        // 2. get bytes of length from in
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        // 3. get object from json str
        RequestPackage request = JSONUtil.toBean(new String(bytes, CharsetUtil.UTF_8), RequestPackage.class);
        out.add(request);
    }
    
}
