package com.twq.rpcFrame.netty.client;

import com.twq.rpcFrame.entity.RpcResponse;

import java.util.Objects;
import java.util.concurrent.*;

/**
 *
 * 存储每次Rpc请求后的 CompletableFuture
 * 可以异步回调获取请求结果
 *
 * @Author: tangwq
 */
public class AsyncRpcFutures {

    /**
     * 存放每次Rpc请求的CompletableFuture， 用于异步获取请求返回值
     */
    private volatile static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> asyncResponseFutures;

    //静态代码块 实现单例对象的创建
    static {
        getExecutorService();
    }

    private static void getExecutorService(){
        if(asyncResponseFutures == null){
            asyncResponseFutures = new ConcurrentHashMap<>();
        }
    }

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        asyncResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        asyncResponseFutures.remove(requestId);
    }

    /**
     * 将响应结果通过MessageID 放入对应的 CompletableFuture，
     * @param rpcResponse
     */
    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = asyncResponseFutures.remove(rpcResponse.getMessageId());
        if (null != future) {
            //调用CompletableFuture的 complete方法 唤醒 get阻塞，获得响应结果。
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
