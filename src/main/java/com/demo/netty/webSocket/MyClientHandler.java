package com.demo.netty.webSocket;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final AtomicInteger seq = new AtomicInteger();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            System.out.println("websocket handshake complete, start long connection ping");
            ctx.writeAndFlush(new TextWebSocketFrame("hello from client"));

            // 每 2 秒发一条，验证长连接还活着
            // initialDelay: 第一次执行的延迟时间 
            // period: 每次执行的间隔时间
            // unit: 时间单位
            ctx.executor().scheduleAtFixedRate(() -> {
                if (!ctx.channel().isActive()) {
                    return;
                }
                String text = "ping#" + seq.incrementAndGet() + " " + LocalDateTime.now();
                ctx.writeAndFlush(new TextWebSocketFrame(text));
                System.out.println("client send: " + text);
            }, 2, 2, TimeUnit.SECONDS);
            return;
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("client received: " + msg.text());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("client exception: " + cause.getMessage());
        ctx.close();
    }
}
