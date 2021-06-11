package com.twq.rpcFrame.loadBanlance;

import com.twq.rpcFrame.entity.RpcService;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡——随机策略
 *
 * @Author: tangwq
 */
public class RandomLoadBalance implements LoadBalance{
    @Override
    public RpcService select(List<RpcService> rpcServiceList) {
        return rpcServiceList.get(new Random().nextInt(rpcServiceList.size()));
    }
}
