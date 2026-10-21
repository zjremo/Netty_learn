package com.demo.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.demo.netty.rpc.RpcClient;

public class TestRpc {
    private static AtomicInteger threadId = new AtomicInteger();
    public static void main(String[] args) {
        // 先单独启动 RpcServer.main，再跑这个客户端
        RpcClient rpcClient = new RpcClient();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            futures.add(executor.submit(() -> {
                int id = threadId.getAndIncrement();
                return rpcClient.invoke("HelloService", "hello", new Object[] {"I am thread" + id});
            }));
        }
        for (Future<?> future : futures) {
            try {
                System.out.println(future.get());
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        executor.shutdown();
        rpcClient.close();
    }
}
