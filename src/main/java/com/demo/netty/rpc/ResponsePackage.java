package com.demo.netty.rpc;

import lombok.Data;

/*
结果响应包

data 数据结果  
code HTTPStatus来表示状态 
cause 错误原因 
*/
@Data 
public class ResponsePackage {
    private Object data;
    private int code;
    private String cause;
}
