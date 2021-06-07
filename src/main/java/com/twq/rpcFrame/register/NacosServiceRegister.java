package com.twq.rpcFrame.register;

import com.twq.rpcFrame.nacos.NacosUtil;

import java.net.InetSocketAddress;

/**
 * Nacos注册服务
 * @Author: tangwq
 */
public class NacosServiceRegister implements ServiceRegister{

    @Override
    public void registerService(String serviceName, InetSocketAddress address) {
        NacosUtil.registService(serviceName, address.getHostName(),address.getPort());
    }
}
