package com.twq.rpcFrame.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @Author: tangwq
 */
public class NettyChannelProvide {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannelProvide.class);

    //启动器
    private static final Bootstrap bootstrap;

    private static Map<String, Channel> channels;



    static {

        channels = new ConcurrentHashMap<>();
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true);
    }

    public synchronized static Channel get(String addressHost, int port) throws ExecutionException, InterruptedException {
        System.out.println(addressHost);
        // InetSocketAddress重写了Equals和toString方法，可以拿他作为key
        String key = addressHost+port;
        if(channels.containsKey(key)){
            System.out.println("The same key has same socket ______________________");
            return channels.get(key);
        }

        bootstrap.handler(new NettyClientInitializer());
        Channel channel = null;
        /**
         * get方法获取执行结果，会阻塞， complete可以唤醒，并传入get所要获得的参数。（类似于wait和single，但是更灵活）
         * get 和 complete 方法 可以实现。
         * 可以通过异步回调的形式
         */
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(addressHost, port).addListener((ChannelFutureListener) future -> { // 这是异步判断连接是否成功。
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                System.out.println("连接客户端成功--------------------------------------------------------------");
                completableFuture.complete(future.channel()); // 连接成功 则将channel放入 唤醒 get阻塞的地方
            } else {
                throw new IllegalStateException();
            }
        });
        channel = completableFuture.get();
        channels.put(key, channel);
        return channel; // 这里的get不会阻塞，completableFuture.complete的时候，get会有真正的返回值，。
    }



}
