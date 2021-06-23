package com.twq.rpcFrame.netty.client;


import com.twq.rpcFrame.entity.RpcResponse;
import io.netty.channel.*;


/**
 * @Author: tangwq
 * @Description:
 */
public class NettyClientHandler extends SimpleChannelInboundHandler{

    private RpcResponse result; //返回的结果


    private AsyncRpcFutures asyncRpcFutures;

    public NettyClientHandler(){
        asyncRpcFutures = new AsyncRpcFutures();
    }

    /**
     * 与服务器创建连接后 自动调用
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{

    }

    /**
     * 收到服务器的数据后 会调用该方法
     * 这里为什么要加 synchronized方法呢 ， 实际上在并发的时候， 有多个消费者要进行相同的rpc请求。
     * 如果当一个RPC没处理完成，另外一个RPC又进来 （都是读取同一个通道里面的值）可能会有问题。
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public  void  channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("处理接收到信息的channel是"+ctx.channel().toString());
        // 获取服务端的响应对象
        result = (RpcResponse) msg;
        // 将响应结果放入对应的completableFuture中。
        asyncRpcFutures.complete(result);
        //唤醒等待的线程
        // notify();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }



}
