package com.rpamis.chain.core.entities;

/**
 * 责任链Handler处理异常
 *
 * @author benym
 * @date 2023/3/5 17:40
 */
public class ChainException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChainException(String message) {
        super(message);
    }

    public ChainException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
