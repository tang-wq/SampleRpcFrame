package com.twq.netty.client;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: tangwq
 * @Date: 2021/05/28/11:28
 * @Description:
 */
public class NettyClient {

    /**
     * 为什么要使用线程池
     * 1、并行处理RPC请求，如果是串行处理，则会第一个RPC请求得到结果后才能进行第二次RPC请求 影响效率。
     * 2、使用其call方法进行RPC调用。很方便。
     */
    // 创建线程池
    private static ExecutorService executorService = new SampleThreadPool().getExecutorService();
    //private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static NettyClientHandler clientHandler;

    // 使用代理模式 获取代理对象
    public Object getBean(final Class<?> serviceClass, final String providerName){



        /**
         * 通过Proxy.newProxyInstance代理方法 获取一个代理对象
         *
         * 为什么要用代理对象， 提升灵活度， 可以RPC请求不同的方法。 RPC请求 通过代理模式在实现得增强。
         */
        Object bean = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass},
                (proxy, method, args)-> { // 代理模式 必须重写这个类的 invoke实现自己的增强逻辑， 调用被代理的方法时会自动调用invoke方法

                    /**
                     * 在代理模式invoke中，实现的 RPC请求的操作。
                     * @param proxy
                     * @param method
                     * @param args
                     * @return
                     * @throws Throwable
                     */
//                    @Override
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        if(clientHandler==null){
                            initClient(); //初始化RPC客户端
                        }
                        // args 实际上就是被代理对象（方法）传入参数
                        clientHandler.setParam(providerName+args[0]);

                        //将任务提交到线程池， 它会自动调用里面的Call方法 ，这就是我们自定义的NettyClientHandler为什么要继承Callable的原因
                        // 调用 Call方法 就执行了 RPC请求 ， 获取线程执行的结果，线程池会自动执行 callable的call方法。
                        return executorService.submit(clientHandler).get();
                   // }
                }
        );

        return bean;
    }

    // 初始化客户端
    private static void initClient(){
        clientHandler = new NettyClientHandler();

        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(clientHandler);
                    }

                });


        try {
            ChannelFuture channelFuture =bootstrap.connect("127.0.0.1",7000).sync();

            // 将Main线程阻塞在这。 https://blog.csdn.net/m0_45406092/article/details/104394617
            //channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
