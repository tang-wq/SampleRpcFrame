package com.twq.rpcFrame.register;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.twq.rpcFrame.entity.RpcService;
import com.twq.rpcFrame.nacos.NacosUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * todo 没有加入负载均衡策略 会对descoverAllServices返回的service做负载均衡，最终只返回一个service对象
 * @Author: tangwq
 */
public class NacosServiceDiscovery implements ServiceDiscovery{
    @Override
    public List<RpcService> descoverAllServices(String serviceName) {
        //获取Nacos注册中心的服务实例
        List<Instance> instances = NacosUtil.getAllServiceInstances(serviceName);
        List<RpcService> services = new ArrayList<>();
        //封装为自定义的实例对象
        for(Instance serviceInstance:instances){
            services.add(RpcService.builder().serviceName(serviceInstance.getServiceName())
                    .addrHost(serviceInstance.getIp()).port(serviceInstance.getPort()).build());
        }

        return services;
    }

    /**
     *
     * @param serviceName 自定义service对象
     * @return
     */
    @Override
    public RpcService getSingleService(String serviceName) {
        //获取Nacos注册中心的服务实例
        Instance serviceInstance =  NacosUtil.getSingleServiceInstance(serviceName);
        if (serviceInstance==null) return null;
        return RpcService.builder().serviceName(serviceInstance.getServiceName())
                .addrHost(serviceInstance.getIp()).port(serviceInstance.getPort()).build();
    }


}
