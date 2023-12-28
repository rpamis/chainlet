package com.rpamis.pattern.chain.entity;

import java.io.Serializable;

/**
 * 降级处理上下文
 *
 * @author benym
 * @date 2023/8/18 15:08
 */
public abstract class FallBackContext<T> implements Serializable {

    private static final long serialVersionUID = 1725393315253761901L;

    /**
     * 责任链处理主数据
     */
    T handlerData;

    /**
     * 责任链处理返回数据
     */
    Object processedData;

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

    public Object getProcessedData() {
        return processedData;
    }

    public void setProcessedData(Object processedData) {
        this.processedData = processedData;
    }

    @Override
    public String toString() {
        return "FallBackContext{" +
                "handlerData=" + handlerData +
                ", processedData=" + processedData +
                ", exceptionOccurred=" + exceptionOccurred +
                '}';
    }
}
