package com.rpamis.pattern.chain.entity;

/**
 * 降级处理上下文
 *
 * @author benym
 * @date 2023/8/18 15:08
 */
public abstract class FallBackContext<T> {

    /**
     * 责任链处理主数据
     */
    T handlerData;

    /**
     * 责任链是否发生异常
     */
    Boolean exceptionOccurred;

    public T getHandlerData() {
        return handlerData;
    }

    public void setHandlerData(T handlerData) {
        this.handlerData = handlerData;
    }

    public Boolean getExceptionOccurred() {
        return exceptionOccurred;
    }

    public void setExceptionOccurred(Boolean exceptionOccurred) {
        this.exceptionOccurred = exceptionOccurred;
    }
}
