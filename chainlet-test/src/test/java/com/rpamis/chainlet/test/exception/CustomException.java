package com.rpamis.chainlet.test.exception;

/**
 * 自定义异常
 *
 * @author benym
 * @since 2024/7/12 16:23
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 6936050760890395523L;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
