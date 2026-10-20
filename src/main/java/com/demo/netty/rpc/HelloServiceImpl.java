package com.demo.netty.rpc;

public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String msg) {
        return "hello, " + msg;
    }
}
