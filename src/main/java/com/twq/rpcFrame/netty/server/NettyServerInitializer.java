package com.twq.rpcFrame.netty.server;

import com.twq.rpcFrame.netty.codec.SelfDefineDecoder;
import com.twq.rpcFrame.netty.codec.SelfDefineEncoder;
import com.twq.rpcFrame.provider.ServiceProvider;
import com.twq.rpcFrame.serializer.HessianSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.AllArgsConstructor;

/**
 * 初始化器
 * @Author: tangwq
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer {

    //这个变量贯穿整个RPC请求的服务端，(所有这个变量 他都是相同的对象)因为需要注册服务接口和实现类的映射。
    private ServiceProvider serviceProvider;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new SelfDefineEncoder(new HessianSerializer()));
        pipeline.addLast(new SelfDefineDecoder());
        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
