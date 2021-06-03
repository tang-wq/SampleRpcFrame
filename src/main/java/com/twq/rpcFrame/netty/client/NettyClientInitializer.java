package com.twq.rpcFrame.netty.client;

import com.twq.rpcFrame.netty.codec.SelfDefineDecoder;
import com.twq.rpcFrame.netty.codec.SelfDefineEncoder;
import com.twq.rpcFrame.serializer.HessianSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 *
 * Netty客户端初始化器
 *
 * 初始化Netty的处理器Handler
 *
 * @Author: tangwq
 */
public class NettyClientInitializer extends ChannelInitializer {


    private NettyClientHandler nettyClientHandler;

    NettyClientInitializer(NettyClientHandler nc){
        // 由初始化函数实例化自定义处理器
        nettyClientHandler = nc;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        // 后期改成变量的形式，可以让使用者通过参数的形式选择编码器中的序列化器
        pipeline.addLast(new SelfDefineEncoder(new HessianSerializer()));
        // 添加解码器
        pipeline.addLast(new SelfDefineDecoder());
        pipeline.addLast(nettyClientHandler);
    }
}
