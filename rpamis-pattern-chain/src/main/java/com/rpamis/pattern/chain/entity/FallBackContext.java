package com.rpamis.pattern.chain.entity;

/**
 * 降级处理上下文
 *
 * @author benym
 * @date 2023/8/18 15:08
 */
public class FallBackContext<T> {

    /**
     * 责任链处理主数据
     */
    T handlerData;

    /**
     * 责任链最终结果实体
     */
    CompleteChainResult completeChainResult;

    /**
     * 责任链异常情况
     */
    Exception exception;

    public FallBackContext(T handlerData, CompleteChainResult completeChainResult, Exception exception) {
        this.handlerData = handlerData;
        this.completeChainResult = completeChainResult;
        this.exception = exception;
    }

    public T getHandlerData() {
        return handlerData;
    }

    public void setHandlerData(T handlerData) {
        this.handlerData = handlerData;
    }

    public CompleteChainResult getCompleteChainResult() {
        return completeChainResult;
    }

    public void setCompleteChainResult(CompleteChainResult completeChainResult) {
        this.completeChainResult = completeChainResult;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "FallBackContext{" +
                "handlerData=" + handlerData +
                ", completeChainResult=" + completeChainResult +
                ", exception=" + exception +
                '}';
    }
}
