package com.twq.rpcFrame.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.twq.rpcFrame.entity.RpcRequest;
import com.twq.rpcFrame.entity.User;
import com.twq.rpcFrame.exception.SerializeException;
import com.twq.rpcFrame.netty.codec.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian序列化器  优点 跨语言
 *
 * 注意 Hessian序列化器 序列化的对象必须实现 java.io.Serializable 接口
 *
 * Hessian反序列化会将所有对象都转换为其序列化时的对象，很方便
 * @Author: tangwq
 */
public class HessianSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭流时有错误发生:", e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessianInput != null) hessianInput.close();
        }
    }

    @Override
    public int getCode() {
        return CommonSerializer.HESSIAN_SERIALIZER;
    }


//    public static void main(String[] args) {
//        HessianSerializer jsonSerializer = new HessianSerializer();
//        User user = new User("TWQ",11);
//        RpcRequest rpcRequest = new RpcRequest("aaaa","bbbbb",new Object[] {"1111",123,user},new Class[]{String.class,Integer.class,User.class});
//        byte [] by = jsonSerializer.serialize(rpcRequest);
//
//        RpcRequest rpcRequest1 = (RpcRequest) jsonSerializer.deserialize(by, RpcRequest.class);
//        System.out.println(rpcRequest1.getInterfaceName());
//    }

}
