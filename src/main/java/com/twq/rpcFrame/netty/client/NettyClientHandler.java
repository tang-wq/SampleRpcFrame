package com.twq.rpcFrame.netty.client;

import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * @Author: tangwq
 * @Description:
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context; //上下文  其他方法会使用这个上下文
    private RpcResponse result; //返回的结果
    private RpcRequest transportParam; //传输数据对象

    /**
     * 与服务器创建连接后 自动调用
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        context = ctx; // 获取连接的上下文。
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
    public synchronized void  channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取服务端的响应对象
        result = (RpcResponse) msg;
        //唤醒等待的线程
        notify();
    }

    /**
     * // 被代理对象调用， 发送数据给服务器，然后 调用wait阻塞，等待数据返回回来 在被唤醒（所以是被channelRead唤醒） 返回服务端的结果给消费者
     *
     * 这里加 synchronized 也是为了在并发时 一个rpc请求会串行处理。 否则会有些问题。 （但是这样是不是效率太低了）
     * @return
     * @throws Exception
     */

    @Override
    public synchronized Object call() throws Exception {

        //发送数据 给服务端。
        context.writeAndFlush(transportParam);
        //进入等待,等待channelRead获取服务端返回值后，被其唤醒
        wait();
        return result.getData();
    }

    /**
     * 设置传递数据的方法
     * @param param
     */
    void setParam(RpcRequest param){
        System.out.println(param);
        this.transportParam = param;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }
}
