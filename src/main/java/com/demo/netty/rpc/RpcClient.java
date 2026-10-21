package com.demo.netty.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;

// 异步结果通知 Result
@Data 
class ResultFuture {
    private RequestPackage requestPackage;
    private CountDownLatch latch = new CountDownLatch(1);
    private ResponsePackage responsePackage;
}

// Rpc client 远程调用客户端
public class RpcClient {
    private String hostAddr;
    private int port;
    private static String defaultHostAddr = "127.0.0.1";
    private static int defaultPort = 7000;
    // netty业务逻辑handler处理 
    private RpcClientHandler rpcClientHandler;
    private EventLoopGroup workerGroup;
    private Channel channel;

    private AtomicLong idGenerator = new AtomicLong();
    private Map<Long, ResultFuture> futureMap;
    private AtomicBoolean isInit = new AtomicBoolean();

    public RpcClient() {
        this(defaultHostAddr, defaultPort);
    }

    public RpcClient(String hostAddr, int port) {
        this.hostAddr = hostAddr;
        this.port = port;
        this.futureMap = new ConcurrentHashMap<>();
    }

    public Object invoke(String serviceName, String methodName, Object[] args) {
        // 1. 初始化 client（多线程下必须等连接建完，不能只 CAS 后立刻往下走）
        ensureInit();

        // 2. 生成编号
        long id = idGenerator.getAndIncrement();

        // 3. 生成请求包
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setRequestId(id);
        requestPackage.setServiceName(serviceName);
        requestPackage.setMethodName(methodName);
        requestPackage.setArgs(args);

        // 4. 注册
        ResultFuture resultFuture = new ResultFuture();
        resultFuture.setRequestPackage(requestPackage);
        futureMap.put(id, resultFuture);

        try {
            return rpcClientHandler.call(id);
        } catch (Exception e) {
            futureMap.remove(id);
            // 不要 e.getCause()：NPE 等无 cause 时会变成空的 RuntimeException，堆栈看不出原因
            throw new RuntimeException(e);
        }
    }

    private void ensureInit() {
        if (isInit.get() && channel != null && channel.isActive()) {
            return;
        }
        synchronized (this) {
            if (isInit.get() && channel != null && channel.isActive()) {
                return;
            }
            initClient();
            isInit.set(true);
        }
    }

    public void initClient() {
        if (channel != null && channel.isActive()) {
            return;
        }
        workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // decoder: decode ResponsePackage
                            pipeline.addLast(new ResponsePackageDecoder());
                            // encoder: encode RequestPackage
                            pipeline.addLast(new RequestPackageEncoder());
                            // ClientHandler
                            pipeline.addLast(rpcClientHandler = new RpcClientHandler(futureMap));
                        }

                    });
            // 只等连接成功，不要在这里关掉 EventLoop，否则通道立刻失效，call() 里 latch 会一直等
            ChannelFuture future = bootstrap.connect(hostAddr, port).sync();
            channel = future.channel();
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
            throw new RuntimeException("rpc client connect interrupted", e);
        }
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
