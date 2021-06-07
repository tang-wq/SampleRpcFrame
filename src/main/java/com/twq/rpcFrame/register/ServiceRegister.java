package com.twq.rpcFrame.register;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;

/**
 * 服务注册
 * @Author: tangwq
 */
public interface ServiceRegister {

    /**
     * 注册服务
     * @param serviceName
     * @param address
     */
    void registerService(String serviceName, InetSocketAddress address);
}
