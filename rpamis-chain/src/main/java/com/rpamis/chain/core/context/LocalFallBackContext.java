package com.rpamis.chain.core.context;

/**
 * 责任链局部降级上下文
 *
 * @author benym
 * @date 2023/8/23 14:16
 */
public class LocalFallBackContext<T> extends FallBackContext<T> {

    private static final long serialVersionUID = 364337999955615297L;

    public LocalFallBackContext(T handlerData, Boolean exceptionOccurred) {
        this.handlerData = handlerData;
        this.exceptionOccurred = exceptionOccurred;
    }
}
