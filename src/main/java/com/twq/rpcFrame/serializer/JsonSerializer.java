package com.twq.rpcFrame.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.RpcResponse;
import com.twq.rpcFrame.entity.User;

/**
 * @Author: tangwq
 */
public class JsonSerializer implements CommonSerializer{
    @Override
    public byte[] serialize(Object obj) {
        byte [] jsonByte = JSON.toJSONBytes(obj);
        return jsonByte;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {

        Object obj= JSON.parseObject(bytes,clazz);
        // 由于反序列化后 RpcRequest和RpcReponse中的param如果存在自定义对象，比如User它会变成JsonObject类型，基础对象不会变比如Integer
        // 因此我们需要进行一次转换。
        // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
        if(obj instanceof RpcRequest){
            RpcRequest rpcRequest = (RpcRequest) obj;
            // 初始化参数Objects
            Object[] objects = new Object[rpcRequest.getParams().length];
            // 对每个参数转换为对应的类型
            for(int i = 0; i < objects.length; i++) {
                Class<?> paramsType = rpcRequest.getParamsTypes()[i];
                // 判断一个变量的类型是不是与其paramsType中的类型对应
                // 我们自定义的类型 就不会对应，所以进行处理， 基础类型是对应的 不用处理
                if (!paramsType.isAssignableFrom(rpcRequest.getParams()[i].getClass())) {
                    objects[i] = JSONObject.toJavaObject((JSONObject) rpcRequest.getParams()[i], rpcRequest.getParamsTypes()[i]);
                } else {
                    objects[i] = rpcRequest.getParams()[i];
                }
            }
            rpcRequest.setParams(objects);
            obj = rpcRequest;
        }else{
            RpcResponse response = JSON.parseObject(bytes, RpcResponse.class);
            Class<?> dataType = response.getDataType();
            // 道理同上
            if(! dataType.isAssignableFrom(response.getData().getClass())){
                response.setData(JSONObject.toJavaObject((JSONObject) response.getData(),dataType));
            }
            obj = response;
        }


        return obj;
    }

    @Override
    public int getCode() {
        return CommonSerializer.JSON_SERIALIZER;
    }

    public static void main(String[] args) {
        JsonSerializer jsonSerializer = new JsonSerializer();
        User user = new User("TWQ",11);
        user.setAge(11);
        user.setName("TWQ");
        RpcRequest rpcRequest = new RpcRequest("aaaa","bbbbb",new Object[] {"1111",123,user},new Class[]{String.class,Integer.class,User.class});
        byte [] by = jsonSerializer.serialize(rpcRequest);

        RpcRequest rpcRequest1 = (RpcRequest) jsonSerializer.deserialize(by, RpcRequest.class);
        System.out.println(rpcRequest1.getInterfaceName());
    }
}

