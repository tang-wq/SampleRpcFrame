package com.twq.rpcFrame.serializer;

/**
 * @Author: tangwq
 * @Description:
 */
public interface CommonSerializer {
    Integer JSON_SERIALIZER = 0;  //Json序列化
    Integer HESSIAN_SERIALIZER = 1; //Hessian序列化反序列化
    Integer PROTOBUF_SERIALIZER = 2; //ProtoBuf序列化反序列化

    static CommonSerializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new JsonSerializer();
            case 1:
                return new HessianSerializer();
            case 2:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }



    /**
     * // 把对象序列化成字节数组
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);



    /**
     * // 其它方式需指定消息格式，再根据message转化成相应的对象
     * // 从字节数组反序列化成消息, 使用java自带序列化方式不用messageType也能得到相应的对象（序列化字节数组里包含类信息）
     * @param bytes
     * @param messageType
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> messageType);


    /**
     * 返回序列化器对应的Code
     * 0、Json 1、Hessian 2、ProtoBuf
     * @return
     */
    int getCode();



}
