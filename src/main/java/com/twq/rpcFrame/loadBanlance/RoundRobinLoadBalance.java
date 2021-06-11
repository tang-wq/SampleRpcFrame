package com.twq.rpcFrame.loadBanlance;

import com.twq.rpcFrame.entity.RpcService;

import java.util.List;

/**
 * 轮询负载均衡
 * @Author: tangwq
 */
public class RoundRobinLoadBalance implements LoadBalance{

    private int index = 0;

    @Override
    public RpcService select(List<RpcService> rpcServiceList) {
        index ++;
        index = index % rpcServiceList.size();
        return rpcServiceList.get(index);
    }
}
