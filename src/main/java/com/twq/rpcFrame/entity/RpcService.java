package com.twq.rpcFrame.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 对注册中心获得的Service的实例封装
 *
 * 因为可能由于不同的注册中心(Zookepper/Nacos)，其返回的对象不同。
 * 为提升拓展性，封装一个通用的Service
 *
 * @Author: tangwq
 */
@AllArgsConstructor
@Data
@Builder
public class RpcService {
    String addrHost;
    int port;
    String serviceName;
}
