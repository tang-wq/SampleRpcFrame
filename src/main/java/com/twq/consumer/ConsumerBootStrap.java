package com.twq.consumer;

import com.twq.netty.client.NettyClient;
import com.twq.publicInterface.HelloService;

/**
 * @Author: tangwq
 * @Date: 2021/05/28/14:36
 * @Description:
 */


public class ConsumerBootStrap {

    //定义传输协议头
    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) throws InterruptedException {
        // 创建一个消费者
        NettyClient customer = new NettyClient();

        // 获取代理对象。
        HelloService helloService = (HelloService) customer.getBean(HelloService.class, providerName);



        for (int i=0;i<2 ;i++ ) {  // 一直RPC请求
            //Thread.sleep(1000);
            //这里实际上时调用代理的hello方法， 会触发 invoke （具体在getBean里面 重写了 invoke方法） ，从而调用call。
            String res = helloService.hello("hello my rpc");
            System.out.println("RPC return is" + res);
        }
    }
}
