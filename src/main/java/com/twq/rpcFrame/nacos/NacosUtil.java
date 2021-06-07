package com.twq.rpcFrame.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.twq.rpcFrame.exception.ExceptionCode;
import com.twq.rpcFrame.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: tangwq
 */
public class NacosUtil {

    // 单例模式（懒汉模式）
    private static final NamingService namingService;

    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final String NACOS_ADDRESS = "192.168.64.163:8848";

    //静态代码块 初始化 Nacos的服务对象
    static {

            namingService = getNamingService();

    }

    private static NamingService getNamingService(){
        try {
            return NamingFactory.createNamingService(NACOS_ADDRESS);
        } catch (NacosException e) {
            logger.error("连接到Nacos失败: ", e);
            throw new RpcException(ExceptionCode.NACOS_FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * 注册服务
     * @param serviceName
     * @param host
     * @param port
     */
    public static void registService(String serviceName, String host, int port){
        try {
            namingService.registerInstance(serviceName,host,port);
        } catch (NacosException e) {
            logger.error("Nacos 注册服务失败: ", e);
            throw new RpcException(ExceptionCode.NACOS_REGISTER_SERVICE_FAILED);
        }
    }

    /**
     * 获取Nacos下的对应serviceName的所有实例（其实就是Nacos定义的服务IP地址端口的包装类集合）
     *
     * 获取后可以根据这些实例进行自定义负载均衡策略
     * @param serviceName
     * @return
     */
    public static List<Instance> getAllServiceInstances(String serviceName){
        try {
            return namingService.getAllInstances(serviceName);
        } catch (NacosException e) {
            logger.error("Nacos 获取服务异常: ", e);
            throw new RpcException(ExceptionCode.NACOS_GET_SERVICE_FAILED);
        }
    }

    /**
     * 获取单个服务实例，（获取Nacos下的哪个服务，由Nacos自带负载均衡确定）
     *
     * todo 这里存在问题， 当服务没有注册到注册中心时，使用selectOneHealthyInstance会报异常。
     *      而我这里在debug时会调用Object的toString（所有对象都继承了Object），导致去查询服务，而Object没有注册在注册中心。
     *
     * @param serviceName
     * @return
     */
    public static Instance getSingleServiceInstance(String serviceName){
        // 预处理一下这个对象
        if(serviceName.equals("java.lang.Object")) return null;
        try {
            return namingService.selectOneHealthyInstance(serviceName);
        } catch (NacosException e) {
            logger.error("Nacos 获取服务异常: ", e);
            throw new RpcException(ExceptionCode.NACOS_GET_SERVICE_FAILED);
        }
    }

    /**
     * 删除服务
     * @param serviceName
     * @param host
     * @param port
     */
    public static void destoryRegistry(String serviceName, String host, int port) {

        try {
            namingService.deregisterInstance(serviceName, host, port);
        } catch (NacosException e) {
            logger.error("注销服务 {} 失败", serviceName, e);
        }

    }

//    public static void main(String[] args) {
//        Object o = NacosUtil.getAllServiceInstances("java.lang.Object");
//        System.out.println(o);
//    }

}
