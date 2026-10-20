package com.demo.netty;

import com.demo.netty.rpc.RpcClient;

public class TestRpc {
    public static void main(String[] args) {
        // 先单独启动 RpcServer.main，再跑这个客户端
        RpcClient rpcClient = new RpcClient();
        try {
            Object result = rpcClient.invoke("HelloService", "hello", new Object[] { "world" });
            System.out.println(result);
        } finally {
            rpcClient.close();
        }
    }
}
