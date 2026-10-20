package com.demo.netty.rpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

// Rpc client 远程调用客户端
public class RpcClient {
    private String hostAddr;
    private int port;
    private static String defaultHostAddr = "127.0.0.1";
    private static int defaultPort = 7000;
    private RpcClientHandler rpcClientHandler;
    private EventLoopGroup workerGroup;
    private Channel channel;

    // 一般还是要使用ThreadPoolExecutor创建
    private ExecutorService executor;

    public RpcClient() {
        this(defaultHostAddr, defaultPort);
    }

    public RpcClient(String hostAddr, int port) {
        this.hostAddr = hostAddr;
        this.port = port;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Object invoke(String serviceName, String methodName, Object[] args) {
        // 1. 初始化client, 建立联系
        initClient();

        // 2. 发送请求，使用线程池异步接收结果
        rpcClientHandler.setServiceName(serviceName);
        rpcClientHandler.setMethodName(methodName);
        rpcClientHandler.setArgs(args);
        try {
            return executor.submit(rpcClientHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getCause());
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
                            pipeline.addLast(rpcClientHandler = new RpcClientHandler());
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
        if (executor != null) {
            executor.shutdown();
        }
    }
}
