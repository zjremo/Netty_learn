package com.demo.netty.pack;

import lombok.Data;

// MyPackage 自定义协议包，解决TCP粘包/拆包问题
@Data
public class MyPackage {
    private int length;
    private byte[] content;
}
