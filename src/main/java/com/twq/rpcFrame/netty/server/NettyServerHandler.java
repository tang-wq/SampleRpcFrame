package com.twq.rpcFrame.netty.server;

import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.RpcResponse;
import com.twq.rpcFrame.netty.ThreadPool.SampleThreadPool;
import com.twq.rpcFrame.provider.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * @Author: tangwq
 * @Description:
 */
@AllArgsConstructor // 自定生成全参的构造函数
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    //这个变量贯穿整个RPC请求的服务端，(所有这个变量 他都是相同的对象)因为需要注册服务接口和实现类的映射。
    private ServiceProvider serviceProvider;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        System.out.println("客户端的IP和端口是："+inetSocketAddress.getAddress()+":"+inetSocketAddress.getPort());

        //服务端必然接收的是RpcRequest
        RpcRequest rpcRequest = (RpcRequest) msg;
        System.out.println(rpcRequest);

        RpcResponse response = doRpcResponse(rpcRequest);
        ctx.writeAndFlush(response);




    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 处理Rpc的请求数据， 并返回响应的值
     * 通过反射机制 调用服务对应的方法
     * @param rpcRequest
     * @return
     */
    public RpcResponse doRpcResponse(RpcRequest rpcRequest) {

        String interfaceName = rpcRequest.getInterfaceName();

        // 这里通过接口名获得对象的话仅仅是接口对象，获取不到实现类对象。 因此还是需要用到映射。
        // Object interfaceObj = Class.forName(interfaceName);
        // 从映射中取得接口的实现类对象
        Object service = serviceProvider.getServiceProvider(interfaceName);

        if (service == null) { // 服务端没有注册该服务
            logger.info("服务端，没有注册该服务");
            return RpcResponse.fail();
        }

        // 反射调用方法（反射包提供）
        Method method;
        try {
            // 获取Rpc所要调用的方法 （反射方式获取）
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsTypes());
            //调用执行对应的方法 （反射方式调用执行）
            Object invoke = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(invoke,rpcRequest.getMessageId());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            logger.info("服务方法执行异常");
            return RpcResponse.fail();
        }

    }
}
