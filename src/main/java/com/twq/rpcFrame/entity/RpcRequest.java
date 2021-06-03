package com.twq.rpcFrame.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 请求对象
 *
 * 对Rpc请求对象进行封装，达到通用的目的。
 *
 * 因为Rpc请求 调用的方法不同和方法对应参数个数以及参数类型都是不固定的，因此我们封装一个通用对象，方便服务端进行解析。
 *
 * @Author: tangwq
 */
@Data // 为参数自动生成 get set 、 toString等方法
@Builder // 可以通过Lombok的语法 初始化对象
@AllArgsConstructor //自动生成全参数构造函数
public class RpcRequest implements Serializable {

    // 服务类名，客户端只知道接口名，在服务端中用接口名指向实现类
    private String interfaceName;
    // 方法名
    private String methodName;
    // 参数列表
    private Object[] params;
    // 参数类型
    private Class<?>[] paramsTypes;
}
