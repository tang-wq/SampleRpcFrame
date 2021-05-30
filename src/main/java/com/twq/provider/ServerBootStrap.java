package com.twq.provider;

import com.twq.netty.server.NettyServer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: tangwq
 * @Description: 服务方启动类
 */
public class ServerBootStrap {

    public static void main(String[] args) {
        //启动服务
        NettyServer.startServer("127.0.0.1",7000);
    }
}
