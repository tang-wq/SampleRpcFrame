package com.twq.rpcFrame.exception;

/**
 * @Author: tangwq
 */
public class RpcException extends RuntimeException{

    public RpcException(ExceptionCode error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(ExceptionCode error) {
        super(error.getMessage());
    }
}
