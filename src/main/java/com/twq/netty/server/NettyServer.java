package com.twq.netty.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Author: tangwq
 * @Description: 
*/


public class NettyServer {


    /**
     * 暴露给外面的启动方法
     * @param host
     * @param port
     */
    public static void startServer(String host, int port){
        startServer0(host,port);
    }

    /**
     * 启动服务的方法
     * @param host
     * @param port
     */
    private static void startServer0(String host, int port){

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new StringDecoder());//解码器
                            pipeline.addLast(new StringEncoder());//编码器
                            pipeline.addLast(new NettyServerHandler()); // 业务处理器
                        }
                    });


            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
            System.out.println("服务方开始提供服务！！！！");
            // 这个语句会阻塞在这里， 不会执行下面的 finally ， 直到出现异常， 才会执行try的finally 将 线程组关闭
            // 如果没有这个语句，那么执行完 bind就会直接执行finally， 则线程组直接全部关闭， 那么Socket就没用了
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
