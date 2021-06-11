package com.twq.rpcFrame.register;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.twq.rpcFrame.entity.RpcService;
import com.twq.rpcFrame.loadBanlance.LoadBalance;
import com.twq.rpcFrame.loadBanlance.RoundRobinLoadBalance;
import com.twq.rpcFrame.nacos.NacosUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * todo 没有加入负载均衡策略 会对descoverAllServices返回的service做负载均衡，最终只返回一个service对象
 * @Author: tangwq
 */
public class NacosServiceDiscovery implements ServiceDiscovery{

    //负载均衡器
    // todo 后期要改成可配置的负载均衡， 这里是写死的 不是很灵活
    private LoadBalance loadBalance = new RoundRobinLoadBalance();

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
     * 使用自定义负载均衡获取服务
     * @param serviceName
     * @return
     */
    public RpcService getServiceBySelfDefinLoadBalance(String serviceName){
        List<RpcService> rpcServiceList = descoverAllServices(serviceName);
        return loadBalance.select(rpcServiceList);
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
