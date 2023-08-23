package com.rpamis.pattern.chain.entity;

/**
 * 责任链局部降级上下文
 *
 * @author benym
 * @date 2023/8/23 14:16
 */
public class LocalFallBackContext<T> extends FallBackContext<T> {

    public LocalFallBackContext(T handlerData, Boolean exceptionOccurred) {
        this.handlerData = handlerData;
        this.exceptionOccurred = exceptionOccurred;
    }
}
