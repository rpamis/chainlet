package com.benym.rpamis.pattern.chain;

/**
 * 责任链Handler处理异常
 *
 * @date 2023/3/5 17:40
 */
public class ChainException extends Exception {

    private static final long serialVersionUID = 1L;

    public ChainException() {
    }

    public ChainException(String message) {
        super(message);
    }

    public ChainException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ChainException(Throwable rootCause) {
        super(rootCause);
    }
}
