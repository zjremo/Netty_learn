package com.demo.netty.rpc;

import lombok.Data;

@Data 
public class RequestPackage {
    private long requestId;
    private String serviceName;
    private String methodName;
    private Object[] args;
}