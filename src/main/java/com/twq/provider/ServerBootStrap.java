package com.twq.provider;

import com.twq.publicInterface.UserService;
import com.twq.rpcFrame.entity.User;
import com.twq.rpcFrame.netty.server.NettyServer;
import com.twq.rpcFrame.provider.ServiceProvider;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: tangwq
 * @Description: 服务方启动类
 */
public class ServerBootStrap {

    public static void main(String[] args) {

        ServiceProvider serviceProvider = new ServiceProvider();
        // 注册服务
        serviceProvider.addServiceProvider(new HelloServiceImpl());
        serviceProvider.addServiceProvider(new UserServiceImpl());
        // 初始化Rpc启动器
        NettyServer nettyServer = new NettyServer(serviceProvider);
        //启动服务
        nettyServer.startServer("127.0.0.1",7000);
    }
}
