package com.twq.rpcFrame.netty.codec;

import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.RpcResponse;
import com.twq.rpcFrame.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * 自定义编码器
 *
 *对消息进行编码
 * 1、使用序列化器对消息进行序列化
 * 2、使用自定义协议，规定传输的数据
 *
 * @Author: tangwq
 */
@AllArgsConstructor // 给这个类生成全参数构造函数
public class SelfDefineEncoder extends MessageToByteEncoder {

    //序列化器 在构造函数中初始化
    private CommonSerializer serializer;

    /**
     * 编码器
     * 依次按照自定义的消息格式写入，传入的数据为request或者response
     *
     * 问 为什么只传消息体的长度，难道协议的其他参数传输时不会出现粘包么？
     * 答：我们不需要考虑消息体内部的粘包啊，因为消息体RpcRequest/RpcReposnse经过序列化反序列化可以转化回其对应的结构，因此不用担心。
     * 粘包出现在上一个报文和下一个报文之间，所以这个消息体内部的粘包问题就不是问题。
     *
     * 但是消息体是 byte字节数组，我们不知道有多长，所以客户端也不知道接收多长，因此我们将长度也传过去，读取时就读取该长度的字节即解决了粘包问题。
     *
     * @param channelHandlerContext
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {

        //自定义协议 1、消息类型 2、序列化器类型 3、消息长度 4、消息体


        // 如果消息是 RpcRequest
        if(msg instanceof RpcRequest){
            // 传输RpcRequest类型
            out.writeShort(MessageType.REQUEST_TYPE.getCode());
        }

        // 如果消息是 RpcRequest
        if(msg instanceof RpcResponse){
            // 传输RpcRequest类型
            out.writeShort(MessageType.RESPONSE_TYPE.getCode());
        }
        // 写入序列化方式
        out.writeShort(serializer.getCode());
        // 进行序列化，得到序列化数组
        byte[] serialize = serializer.serialize(msg);
        // 写入长度
        out.writeInt(serialize.length);
        // 写入序列化字节数组
        out.writeBytes(serialize);

    }
}
