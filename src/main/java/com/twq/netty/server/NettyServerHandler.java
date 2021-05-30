package com.twq.netty.server;


import com.twq.provider.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author: tangwq
 * @Description:
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 获取客户端发送的RPC请求，并调用服务端API。
        // 客户端在调用服务器API时，需要定义一个协议。（即RPC请求须有一定的规范， 可写可不写）
        // 这里要求其必须以 helloService#hello#开头
        if(msg.toString().startsWith("HelloService#hello#")){
            String res = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#")+1));
            ctx.writeAndFlush(res);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
