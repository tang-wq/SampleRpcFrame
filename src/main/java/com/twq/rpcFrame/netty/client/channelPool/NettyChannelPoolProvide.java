package com.twq.rpcFrame.netty.client.channelPool;

import com.twq.rpcFrame.netty.client.Idle.ClientIdleStateTrigger;
import com.twq.rpcFrame.netty.client.NettyChannelProvide;
import com.twq.rpcFrame.netty.client.NettyClientHandler;
import com.twq.rpcFrame.netty.client.NettyClientInitializer;
import com.twq.rpcFrame.netty.codec.SelfDefineDecoder;
import com.twq.rpcFrame.netty.codec.SelfDefineEncoder;
import com.twq.rpcFrame.serializer.HessianSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 使用Netty自带的ChannelPool
 *
 * 实现为每一个目标服务器建立大小为n的channel链接池，即可以实现channel的复用，又可以提高每一个目标服务器请求的并发处理
 *
 * @Author: tangwq
 */
public class NettyChannelPoolProvide implements ChannelPool{
    private static final Logger logger = LoggerFactory.getLogger(NettyChannelPoolProvide.class);

    // channelPool的Map集合， 一个地址对应一个连接池
    /**
     * ChannelPoolMap
     * 当我们 通过ChannelPoolMap的get获取对ChannelPool的时候，如果没有
     * 则会自动调用我们重写的newPool方法去创建chanelPool。
     */
    private static ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    //启动器
    private static final Bootstrap bootstrap;


    static {
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true);

        initPool(); // 初始化PoolMap
    }


    private static void initPool(){
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {

            /**
             * 当创建channelPool的时候 会自动调用我们这个重写的 newPool方法
             * @param key
             * @return
             */
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {


                ChannelPoolHandler handler = new ChannelPoolHandler() {

                    /**
                     * 释放Channel
                     * @param channel
                     * @throws Exception
                     */
                    @Override
                    public void channelReleased(Channel channel) throws Exception {
                        System.out.println("channel Released .....");
                    }

                    @Override
                    public void channelAcquired(Channel channel) throws Exception {
                        System.out.println("channel Acquire .....");
                    }

                    /**
                     * 之前是直接通过bootstrap.addHandler添加处理器，
                     * 而FixedChannelPool中可以创建链接，因此在创建连接时会调用这个方法，因此在这里添加Handler
                     * 效果等同于NettyClientInitializer的initChannel一样。
                     *
                     * 当链接创建的时候添加channelhandler，只有当channel不足时会创建，但不会超过限制的最大channel数
                     * @param channel
                     * @throws Exception
                     */
                    @Override
                    public void channelCreated(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        //添加心跳处理器，读操作5S后超时。
                        pipeline.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));

                        // 后期改成变量的形式，可以让使用者通过参数的形式选择编码器中的序列化器
                        pipeline.addLast(new SelfDefineEncoder(new HessianSerializer()));

                        // 添加解码器
                        pipeline.addLast(new SelfDefineDecoder());
                        pipeline.addLast(new NettyClientHandler());
                        pipeline.addLast(new ClientIdleStateTrigger());
                    }
                };
                // 为一个目标服务器建立大小为2的channel池
             return new FixedChannelPool(bootstrap.remoteAddress(key), handler, 2);
            }

        };
    }

    @Override
    public Channel getChannel(InetSocketAddress address) throws ExecutionException, InterruptedException {

        /**
         * 当我们使用get方法获取对用地址的channelPool时，如果不存在，底层会直接调用我们上面重写的NewPool方法创建新的Pool并返回
         * 如果存在则直接返回pool。
         */
        SimpleChannelPool channelPool = poolMap.get(address);

        //从池子中获取一个Future对象 （这个Future对象是Netty封装的Future）
        // 因为是异步建立连接 所以返回Future对象。
        Future<Channel> channelFuture = channelPool.acquire();
        channelFuture.addListener((FutureListener<Channel>) f1 -> {
                    if (f1.isSuccess()) {
                        logger.info(address.getHostName()+":建立连接成功");
                    }
                });

        Channel channel = channelFuture.get();
        return channel;
    }

    /**
     * 将Channel释放回channelPool
     * 释放回去才能再次使用
     */
    public void releaseChannel(Channel channel, InetSocketAddress address){
        SimpleChannelPool channelPool = poolMap.get(address);
        channelPool.release(channel);
    }

    @Override
    public Boolean removeChannel() {
        return null;
    }
}
