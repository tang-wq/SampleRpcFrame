package com.twq.rpcFrame.register;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.twq.rpcFrame.entity.RpcService;

import java.util.List;

/**
 * 发现服务
 * @Author: tangwq
 */
public interface ServiceDiscovery {

    /**
     * 发现全部服务
     * @param serviceName
     * @return
     */
    List<RpcService> descoverAllServices(String serviceName);

    /**
     * 获取单个Service
     * @param serviceName
     * @return
     */
    RpcService getSingleService(String serviceName);
}
