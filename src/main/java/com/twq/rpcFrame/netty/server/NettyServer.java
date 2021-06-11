package com.twq.rpcFrame.netty.server;


import com.twq.rpcFrame.netty.client.NettyClientInitializer;
import com.twq.rpcFrame.provider.ServiceProvider;
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
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.AllArgsConstructor;

/**
 * Rpc服务
 * @Author: tangwq
 * @Description: 
*/
@AllArgsConstructor //自动生成全参构造函数
public class NettyServer {

    //这个变量贯穿整个RPC请求的服务端，(所有这个变量 他都是相同的对象)因为需要注册服务接口和实现类的映射。
    private ServiceProvider serviceProvider;

    private static EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(5);


    /**
     * 暴露给外面的启动方法
     * @param host
     * @param port
     */
    public  void startServer(String host, int port){
        startServer0(host,port);
    }

    /**
     * 启动服务的方法
     * @param host
     * @param port
     */
    private  void startServer0(String host, int port){

        EventLoopGroup bossGroup = new NioEventLoopGroup(1); //I/O线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // I/O线程池


        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serviceProvider,eventExecutors));


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
