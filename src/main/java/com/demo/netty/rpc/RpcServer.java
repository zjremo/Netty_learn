package com.demo.netty.rpc;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.http.HttpStatus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j (topic = "rpc.RpcServer")
public class RpcServer {
    // 服务注册表
    private Map<String, Class<?>> registry;
    private int defaultInitialCapacity = 16;
    private String defaultHostAddr = "127.0.0.1";
    private int defaultPort = 7000;

    public RpcServer() {
        registry = new ConcurrentHashMap<>(defaultInitialCapacity);
    }

    public void register(String service, Class<?> serviceClass) {
        registry.put(service, serviceClass);
    }

    public void remove(String service) {
        registry.remove(service);
    }

    public boolean contains(String service) {
        return registry.containsKey(service);
    }

    // 内部类: RpcServerHandler, 处理请求
    private class RpcServerHandler extends SimpleChannelInboundHandler<RequestPackage> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RequestPackage msg) {
            String service = msg.getServiceName();

            ResponsePackage resp = new ResponsePackage();
            if (!RpcServer.this.contains(service)) {
                resp.setData(null);
                resp.setCause("service not exist, serviceName: " + service);
                resp.setCode(HttpStatus.HTTP_NOT_FOUND);
                ctx.writeAndFlush(resp);
                return;
            }

            Class<?> callService = RpcServer.this.registry.get(service);
            String methodName = msg.getMethodName();
            Object[] args = msg.getArgs() == null ? new Object[0] : msg.getArgs();
            try {
                // getMethod(name) 只找无参方法；hello(String) 必须带参数类型
                Class<?>[] paramTypes = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramTypes[i] = args[i].getClass();
                }
                Method method = callService.getMethod(methodName, paramTypes);
                Object result = method.invoke(callService.getDeclaredConstructor().newInstance(), args);
                resp.setData(result);
                resp.setCode(HttpStatus.HTTP_OK);
                ctx.writeAndFlush(resp);
                return;
            } catch (NoSuchMethodException | SecurityException e) {
                resp.setData(null);
                resp.setCause("method not exist, methodName: " + methodName);
                resp.setCode(HttpStatus.HTTP_NOT_FOUND);
                ctx.writeAndFlush(resp);
                return;
            } catch (Exception e) {
                resp.setData(null);
                resp.setCause("invoke method error, methodName: " + methodName + ", cause: " + e.getMessage());
                resp.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
                ctx.writeAndFlush(resp);
                return;
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.info("RpcServerHandler exception cause: {}", cause.getMessage());
            ctx.close();
        }

    }

    // start RpcServer with default config
    public void run() {
        this.run(defaultHostAddr, defaultPort);
    }

    // start RpcServer with options
    public void run(String host, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // decoder: decode RequestPackage
                            pipeline.addLast(new RequestPackageDecoder());
                            // encoder: encode ResponsePackage
                            pipeline.addLast(new ResponsePackageEncoder());
                            // ServerHandler
                            pipeline.addLast(new RpcServerHandler());
                        }

                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.register("HelloService", HelloServiceImpl.class);
        rpcServer.run("127.0.0.1", 7000);
    }
}
