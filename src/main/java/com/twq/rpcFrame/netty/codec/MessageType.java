package com.twq.rpcFrame.netty.codec;

import lombok.AllArgsConstructor;

/**
 * 传输消息的类型
 *
 * 目前主要是我们自定义的俩个传输消息类型
 * 后期可以根据需求在添加
 *
 * @Author: tangwq
 */

@AllArgsConstructor
public enum MessageType {
    // 这是枚举类， 而REQUEST_TYPE，RESPONSE_TYPE相当于俩个类
    // 下面定义的参数也是这俩个类中的参数
    //AllArgsConstructor 此时就是为REQUEST_TYPE，RESPONSE_TYPE这俩个类定义构造函数了。
    REQUEST_TYPE(0),RESPONSE_TYPE(1);

    private int code;

    public int getCode(){
        return code;
    }
}
