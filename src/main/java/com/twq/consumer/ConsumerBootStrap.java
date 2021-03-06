package com.twq.consumer;

import com.twq.publicInterface.UserService;
import com.twq.rpcFrame.entity.User;
import com.twq.rpcFrame.netty.client.NettyClient;
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
        //HelloService helloService = (HelloService) customer.getBean(HelloService.class);
        UserService userService = (UserService) customer.getBean(UserService.class);
        HelloService helloService = (HelloService) customer.getBean(HelloService.class);
//        Object res = userService.getUser();
//        System.out.println("RPC return is" + res);
//        Object res2 = userService.getUser();
//        System.out.println("RPC return is" + res2);
//        String res1 = helloService.hello("hello");
//        System.out.println("RPC hello return is " + res1);


        for (int i=0;i<2 ;i++ ) {  // 一直RPC请求

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Thread.sleep(10000);
                    //这里实际上时调用代理的hello方法， 会触发 invoke （具体在getBean里面 重写了 invoke方法） ，从而调用call。
                    Object res = userService.getUser();
                    System.out.println("RPC return is" + res);
                }
            });
            thread.start();
        }
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //Thread.sleep(10000);
                //这里实际上时调用代理的hello方法， 会触发 invoke （具体在getBean里面 重写了 invoke方法） ，从而调用call。
                Object res1 = helloService.hello("hello");
                System.out.println("RPC hello return is " + res1);
            }
        });
        thread1.start();

    }
}
