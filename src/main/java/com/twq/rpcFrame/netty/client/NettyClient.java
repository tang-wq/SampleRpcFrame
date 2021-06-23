package com.twq.rpcFrame.netty.client;

import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.RpcResponse;
import com.twq.rpcFrame.entity.RpcService;
import com.twq.rpcFrame.netty.ThreadPool.SampleThreadPool;
import com.twq.rpcFrame.netty.client.channelPool.ChannelPool;
import com.twq.rpcFrame.netty.client.channelPool.NettyChannelPoolProvide;
import com.twq.rpcFrame.register.NacosServiceDiscovery;
import io.netty.channel.*;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Author: tangwq
 * @Date: 2021/05/28/11:28
 * @Description:
 */
public class NettyClient{

    /**
     * 为什么要使用线程池
     * 1、并行处理RPC请求，如果是串行处理，则会第一个RPC请求得到结果后才能进行第二次RPC请求 影响效率。
     * 2、使用其call方法进行RPC调用。很方便。
     */
    // 创建线程池（业务线程池）
    private static ExecutorService executorService = new SampleThreadPool().getExecutorService();
    //private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    private  NacosServiceDiscovery nacosServiceDiscovery;

    private AsyncRpcFutures asyncRpcFutures;

    private ChannelPool channelPool;


    public NettyClient(){
        nacosServiceDiscovery = new NacosServiceDiscovery();
        asyncRpcFutures = new AsyncRpcFutures();
        channelPool = new NettyChannelPoolProvide();
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

                    System.out.println(method.getDeclaringClass().getName());
                    RpcRequest rpcRequest = RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                            .methodName(method.getName()).params(args).paramsTypes(method.getParameterTypes()).messageId(UUID.randomUUID().toString()).build();


                    CompletableFuture<RpcResponse> completeFuture;

                    // 从Nacos注册中心获取服务的信息
                    RpcService rpcService = nacosServiceDiscovery.getSingleService(method.getDeclaringClass().getName());

                    if (rpcService == null) {
                        return null;
                    }
                    //将任务提交到线程池， 它会自动调用里面的Call方法 ，这就是我们自定义的NettyClientHandler为什么要继承Callable的原因
                    // 线程池会自动执行 callable的call方法，在clientHandler中重写了Call方法，会发送参数到服务端
                    completeFuture = sendRequest(rpcRequest, rpcService);
                    // 当没有数据返回时 get方法会阻塞在这里， 在收到服务端返回数据时，在Handler中read方法中写了Complete方法
                    // 将返回的数据放入到completeFuture中，并唤醒这里的get 获取这个返回的数据
                    RpcResponse response = completeFuture.get();
                    return response.getData();
                });
        return bean;
    }

    /**
     * 发送RPC请求
     * @param rpcRequest
     * @param rpcService
     * @return
     */
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest, RpcService rpcService){
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            // Channel channel = NettyChannelProvide.get(rpcService.getAddrHost(), rpcService.getPort());
            InetSocketAddress inetSocketAddress = new InetSocketAddress(rpcService.getAddrHost(),rpcService.getPort());
            Channel channel = channelPool.getChannel(inetSocketAddress);
            System.out.println("发送的数据为：" + rpcRequest);
            // 发送数据
            channel.writeAndFlush(rpcRequest).sync();
            asyncRpcFutures.put(rpcRequest.getMessageId(),resultFuture);
            // channel在这里释放回channelPool也可以，并不是说channel关闭了， 他还是监听者是否有数据传过来，并且出发channelRead。
            // 因此还可以接收到对应的返回值。
            channelPool.releaseChannel(channel,inetSocketAddress);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultFuture;
    }

}
