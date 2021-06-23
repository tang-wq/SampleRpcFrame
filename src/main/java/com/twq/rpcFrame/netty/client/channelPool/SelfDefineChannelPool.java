package com.twq.rpcFrame.netty.client.channelPool;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * 自定义 Channel池
 * todo  细节不太好实现。 待做
 * @Author: tangwq
 */
public class SelfDefineChannelPool implements ChannelPool{
    @Override
    public Channel getChannel(InetSocketAddress address) {
        return null;
    }

    @Override
    public void releaseChannel(Channel channel, InetSocketAddress address) {

    }

    @Override
    public Boolean removeChannel() {
        return null;
    }
}
