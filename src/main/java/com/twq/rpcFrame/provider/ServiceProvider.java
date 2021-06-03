package com.twq.rpcFrame.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 服务提供
 *
 * 将需要暴露给消费者的服务 保存在本地服务器上，方便Rpc请求过来后，解析服务
 * 1、提供暴露服务功能，
 * （其实就是在本地先注册对应的服务类，与注册中心的注册服务不同。这里是为了调用服务对应的方法，注册中心（zookepper/nocas）主要是为了找到服务的所在地）
 *  客户端他只知道接口，而不知道他具体的实现类。 因此本地存储的是接口和实现类的映射
 *
 *  todo  这里存在一个问题没处理，就是当一个服务接口有多个实现类(接口多实现)，那么该如何处理。 目前默认一个服务接口只有一个实现类。
 *
 *
 * 2、todo 本地注册完成后，需要向注册中心Zookepper/nocas在注册服务所在的地址。
 *
 * 3、提供Rpc请求后返回对应的服务。
 *
 *
 *
 * @Author: tangwq
 */
public class ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProvider.class);
    // 提供接口名与服务名的映射 key是接口名，value是对应的接口
    // 客户端他只知道接口，而不知道他具体的实现类。 因此存储的是接口和实现类的映射
    // 这里默认了一个服务接口只有一个实现类。
    Map<String, Object> serviceInterface;

    public ServiceProvider(){
        serviceInterface = new ConcurrentHashMap<>();
    }

    public void addServiceProvider(Object service){
        // 这个实现类可能实现了多个接口。（推荐一个实现类只实现了一个接口）
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length==0){
            logger.info("该类不是实现类，无实现的接口，无法注册对应的服务");
            return;
        }

        for(Class clazz: interfaces){
            // 存储服务接口和实现类的映射
            serviceInterface.put(clazz.getName(),service);
            logger.info("向接口: {} 注册服务: {}", clazz.getName(),service.getClass().getName());
        }
    }

    public Object getServiceProvider(String serviceName){
        return serviceInterface.get(serviceName);
    }


}
