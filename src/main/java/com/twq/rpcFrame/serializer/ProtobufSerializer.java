package com.twq.rpcFrame.serializer;

/**
 *
 * 基于protobuf的序列化器
 * @Author: tangwq
 */
public class ProtobufSerializer implements CommonSerializer{
    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> messageType) {
        return null;
    }

    @Override
    public int getCode() {
        return 0;
    }
}
