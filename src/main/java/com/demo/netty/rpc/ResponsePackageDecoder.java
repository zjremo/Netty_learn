package com.demo.netty.rpc;

import java.util.List;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class ResponsePackageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. get length from in
        int length = in.readInt();

        // 2. get bytes of length from in
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        // 3. get object from json str
        ResponsePackage resp = JSONUtil.toBean(new String(bytes, CharsetUtil.UTF_8), ResponsePackage.class);
        out.add(resp);
    }

}
