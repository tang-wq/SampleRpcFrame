package com.twq.rpcFrame.netty.client.Idle;

import com.twq.rpcFrame.entity.RpcRequest;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端的心跳事件捕获器
 *  用于捕获{@link IdleState#WRITER_IDLE}事件（未在指定时间内向服务器发送数据），然后向<code>Server</code>端发送一个心跳包。
 *  作用：当客户端检测如果未在设定的时间内没有向服务器发送过数据，则向服务器发送心跳包，防止服务器与客户端断开连接。
 *
 *
 * @Author: tangwq
 */
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientIdleStateTrigger.class);
    public static final String HEART_BEAT = "heart beat";

    /**
     * userEventTriggered用于捕获各种事件
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //捕获心跳事件
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 如果是写超时事件
            if (state == IdleState.WRITER_IDLE) {
                logger.info("发送心跳包 [{}]", ctx.channel().hashCode());
                RpcRequest rpcRequest = RpcRequest.builder().isHeartBeat(true).build();
                // 向server 发送心跳包。
                ctx.writeAndFlush(rpcRequest);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 出现异常时会触发异常事件，调用该方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 关闭channel
        ctx.close();
    }
}
