package com.twq.rpcFrame.netty.server.idle;

import com.twq.rpcFrame.entity.RpcRequest;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端的心跳事件捕获器
 *  用于捕获{@link IdleState#WRITER_IDLE}事件（未在指定时间内向服务器发送数据），然后向<code>Server</code>端发送一个心跳包。
 *  作用：当服务端检测如果未在设定的时间内没有收到消费端发送过数据，则认为客户端已经出现问题（宕机，或者不在消费），则关闭与客户端的连接，节省资源。
 *
 *
 * @Author: tangwq
 */
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerIdleStateTrigger.class);

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
            // 如果是读超时事件
            if (state == IdleState.READER_IDLE) {
                logger.info("客户端长时间没有操作，断开连接");
                // 关闭连接
                ctx.disconnect();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
