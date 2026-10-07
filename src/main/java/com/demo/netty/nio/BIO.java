package com.demo.netty.nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "C.BIO")
public class BIO {
    private static int serverPort = 6666;

    public static void main(String[] args) {
        /*
         * BIO 同步阻塞
         * 核心: 来一个请求就创建一个线程去处理；
         */
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(serverPort)) {
            log.debug("Server open service, port: {}", serverPort);
            while (true) {
                log.debug("wait for connect...");
                final Socket socket = server.accept();
                log.debug("connect a client successfully");
                threadPool.submit(() -> handler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void handler(Socket socket) {
        try {
            log.debug("ThreadId: {}, ThreadName: {}", Thread.currentThread().threadId(),
                    Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true) {
                log.debug("read...");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    log.debug(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        } finally {
            log.debug("close connection with client");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
