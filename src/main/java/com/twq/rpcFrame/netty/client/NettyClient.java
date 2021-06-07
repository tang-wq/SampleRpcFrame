package com.twq.rpcFrame.netty.client;

import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.RpcService;
import com.twq.rpcFrame.netty.ThreadPool.SampleThreadPool;
import com.twq.rpcFrame.register.NacosServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    //private static ExecutorService executorService = new SampleThreadPool().getExecutorService();
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler clientHandler;

    //启动器
    private static final Bootstrap bootstrap;

    private  NacosServiceDiscovery nacosServiceDiscovery;


    //从注册中心获得的Service对象
    private RpcService rpcService;

    // 静态代码块 初始化一部分Netty客户端配置（代码的复用）。
    static {


        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true);
                //.handler(new NettyClientInitializer(clientHandler)); //初始化器
    }

    public NettyClient(){
        nacosServiceDiscovery = new NacosServiceDiscovery();
    }

    // 使用代理模式 获取代理对象
    public Object getBean(final Class<?> serviceClass){


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


                        // 初始化RpcRequest对象， 并赋值。 使用了lombok中的builder，代码简洁
                        // 获得请求的1、接口名 2、方法名 3、方法的参数 4、参数类型
                        System.out.println(method.getDeclaringClass().getName());
                        RpcRequest rpcRequest = RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                                .methodName(method.getName()).params(args).paramsTypes(method.getParameterTypes()).build();

                        // 从Nacos注册中心获取服务的信息
                        rpcService = nacosServiceDiscovery.getSingleService(method.getDeclaringClass().getName());
                        if(rpcService==null){
                            return null;
                        }
                        //启动Rpc连接   （相同的IP和相同的端口，有必要重复调用连接么）
                        clientHandler = new NettyClientHandler();
                        bootstrap.handler(new NettyClientInitializer(clientHandler)); //初始化器
                        bootstrap.connect(rpcService.getAddrHost(),rpcService.getPort()).sync();


                        // args 实际上就是被代理对象（方法）传入参数
                        clientHandler.setParam(rpcRequest);

                        //将任务提交到线程池， 它会自动调用里面的Call方法 ，这就是我们自定义的NettyClientHandler为什么要继承Callable的原因
                        // 线程池会自动执行 callable的call方法，在clientHandler中重写了Call方法，会发送参数到服务端
                        return executorService.submit(clientHandler).get();
                   // }
                }
        );

        return bean;
    }

}
