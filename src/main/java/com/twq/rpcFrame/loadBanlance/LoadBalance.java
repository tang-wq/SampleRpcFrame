package com.twq.rpcFrame.loadBanlance;

import com.twq.rpcFrame.entity.RpcService;

import java.util.List;

/**
 * 负载均衡接口
 *
 * 其他负载均衡的具体实现都实现这个接口
 *
 * @Author: tangwq
 */
public interface LoadBalance {

    Integer RANDOM_LOAD_BALANCE = 1; //随机负载均衡
    Integer WIGHT_LOAD_BALANCE = 2; // 权重负载均衡  todo 待实现
    Integer ROUND_ROBIN_LOAD_BALANCE = 3; // 轮询负载均衡

    /**
     * 负载均衡算法 选择服务。
     * @param rpcServiceList
     * @return
     */
    RpcService select(List<RpcService> rpcServiceList);

}
