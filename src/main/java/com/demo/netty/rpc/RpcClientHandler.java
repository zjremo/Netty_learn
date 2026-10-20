package com.demo.netty.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import cn.hutool.http.HttpStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j (topic = "rpc.RpcClientHandler")
@Setter 
@Getter 
public class RpcClientHandler extends SimpleChannelInboundHandler<ResponsePackage> implements Callable<Object>{
    private ChannelHandlerContext context; // 获取连接通道上下文，方便后续rpc调用
    private String serviceName;
    private String methodName;
    private Object[] args;
    private CountDownLatch latch;
    private ResponsePackage responsePackage;

    public RpcClientHandler() {
        this.context = null;
        this.responsePackage = null;
        this.latch = null;
    }

    public RpcClientHandler(String serviceName, String methodName, Object[] args) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.args = args;
        this.context = null;
        this.responsePackage = null;
        this.latch = null;
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
        this.responsePackage = msg;
        if (latch != null) {
            latch.countDown();
        }
    }

    @Override
    public Object call() throws Exception {
        log.info("RpcClientHandler call invoke...");
        // 等 channelActive 把 context 设好
        activeLatch.await();
        latch = new CountDownLatch(1);
        RequestPackage req = new RequestPackage();
        req.setServiceName(serviceName);
        req.setMethodName(methodName);
        req.setArgs(args);
        context.writeAndFlush(req);
        latch.await();
        // 解析responsePackage
        if (responsePackage.getCode() == HttpStatus.HTTP_OK) {
            return responsePackage.getData();
        } else {
            throw new RuntimeException(String.format("%s.%s invoke error, HTTP status code: %d, cause: %s", serviceName, methodName, responsePackage.getCode(), responsePackage.getCause()));
        }
    }

}
