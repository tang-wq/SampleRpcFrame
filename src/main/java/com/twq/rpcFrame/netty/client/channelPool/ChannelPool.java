package com.twq.rpcFrame.netty.client.channelPool;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 * ChannelPool 接口
 * @Author: tangwq
 */
public interface ChannelPool {
    /**
     * 获取Channel
     * @return
     */
    public  Channel getChannel(InetSocketAddress address) throws ExecutionException, InterruptedException;

    /**
     * 将channel释放回channel池
     * @param channel
     * @param address
     */
    public void releaseChannel(Channel channel, InetSocketAddress address);
    /**
     * 移除Channel
     * @return
     */
    Boolean removeChannel();
}
