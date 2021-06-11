package com.twq.rpcFrame.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * 响应对象
 *
 * 对Rpc请求服务端的响应进行封装， 因为接口API返回值是不同的，可能是参数，可能是对象
 * 对其进行封装，可以不仅能方便客户端解析，而且还能达到通用的目的
 *
 * @Author: tangwq
 */
@Data
@Builder
@AllArgsConstructor
public class RpcResponse implements Serializable {
    // 响应状态码
    private int code;
    private String message;
    // 消息ID  用于与请求Meeeage匹配
    private String messageId;
    // 这里我们需要加入这个，不然用其它序列化方式（除了java Serialize）得不到data的type
    private Class<?> dataType;
    private Object data;

    public static RpcResponse success(Object data, String messageId) {
        return RpcResponse.builder().code(200).data(data).dataType(data.getClass()).messageId(messageId).build();
    }
    public static RpcResponse fail() {
        return RpcResponse.builder().code(500).message("服务器发生错误").build();
    }

}
