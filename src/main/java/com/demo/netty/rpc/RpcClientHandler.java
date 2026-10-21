package com.demo.netty.rpc;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import cn.hutool.http.HttpStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "rpc.RpcClientHandler")
@Setter
@Getter
public class RpcClientHandler extends SimpleChannelInboundHandler<ResponsePackage> {
    private ChannelHandlerContext context; // 获取连接通道上下文，方便后续rpc调用
    private Map<Long, ResultFuture> futureMap;

    public RpcClientHandler() {
        this.context = null;
    }

    public RpcClientHandler(Map<Long, ResultFuture> futureMap) {
        this.futureMap = futureMap;
    }

    private final CountDownLatch activeLatch = new CountDownLatch(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("rpc client handler channel active...");
        this.context = ctx;
        activeLatch.countDown();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponsePackage msg) throws Exception {
        long requestId = msg.getRequestId();
        ResultFuture rf = futureMap.remove(requestId);
        if (rf != null) {
            // 必须先塞回响应，再 countDown；否则 await 醒来后 responsePackage 仍是 null
            rf.setResponsePackage(msg);
            rf.getLatch().countDown();
        }
    }

    public Object call(long requestId) throws Exception {
        log.info("RpcClientHandler call invoke...");
        // 等 channelActive 把 context 设好
        activeLatch.await();

        ResultFuture rf = futureMap.get(requestId);
        if (rf == null) {
            throw new RuntimeException("requestId not exist...");
        }

        context.writeAndFlush(rf.getRequestPackage());
        rf.getLatch().await();

        String serviceName = rf.getRequestPackage().getServiceName();
        String methodName = rf.getRequestPackage().getMethodName();

        ResponsePackage responsePackage = rf.getResponsePackage();
        if (responsePackage.getCode() == HttpStatus.HTTP_OK) {
            return responsePackage.getData();
        } else {
            throw new RuntimeException(String.format("%s.%s invoke error, HTTP status code: %d, cause: %s", serviceName, methodName, responsePackage.getCode(), responsePackage.getCause()));
        }
    }

}
