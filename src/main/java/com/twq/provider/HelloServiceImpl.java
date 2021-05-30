package com.twq.provider;

import com.twq.publicInterface.HelloService;

/**
 *  服务方方法
 * @Author: tangwq
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        if(msg !=null){
            return "已收到消息："+msg;
        }
        return "已收到消息：消息为空";
    }
}
