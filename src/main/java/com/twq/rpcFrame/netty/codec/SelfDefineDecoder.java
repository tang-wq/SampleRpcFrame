package com.twq.rpcFrame.netty.codec;

import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.RpcResponse;
import com.twq.rpcFrame.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 自定义解码器
 *
 * 将序列化器加入到解码器中 对传输数据过来的进行解码
 *
 * @Author: tangwq
 */
public class SelfDefineDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(SelfDefineDecoder.class);


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 1. 读取消息类型
        short messageType = in.readShort();
        // 现在还只支持request与response请求
        // 消息的类型
        Class<?> messageClass;
        if(messageType == MessageType.REQUEST_TYPE.getCode()){
            messageClass = RpcRequest.class;
        }else if(messageType == MessageType.RESPONSE_TYPE.getCode()){
            messageClass = RpcResponse.class;
        }else{
            logger.error("不识别的数据包: {}", messageType);
            return;
        }

        /**
         * 读取顺序 就是自定义协议传输的顺序
         * 1、消息类型 2、序列化器类型 3、消息长度 4、消息体
         */
        // 2. 读取序列化的类型
        short serializerType = in.readShort();
        // 根据类型得到相应的序列化器
        CommonSerializer serializer = CommonSerializer.getSerializerByCode(serializerType);
        if(serializer == null)throw new RuntimeException("不存在对应的序列化器");
        // 3. 读取数据序列化后的字节长度
        int length = in.readInt();
        // 4. 读取序列化数组
        byte[] bytes = new byte[length];
        // 将数据读入到bytes数组中
        in.readBytes(bytes);
        // 用对应的序列化器解码字节数组
        Object deserialize = serializer.deserialize(bytes, messageClass);
        out.add(deserialize);
    }
}
