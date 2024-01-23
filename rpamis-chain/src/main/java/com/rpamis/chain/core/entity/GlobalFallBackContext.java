package com.rpamis.chain.core.entity;

/**
 * 责任链全局降级上下文
 *
 * @author benym
 * @date 2023/8/23 14:17
 */
public class GlobalFallBackContext<T> extends FallBackContext<T> {

    private static final long serialVersionUID = -7138806610509549353L;

    /**
     * 责任链最终结果实体
     */
    CompleteChainResult completeChainResult;

    public GlobalFallBackContext(T handlerData, Object processedData, CompleteChainResult completeChainResult, Boolean exceptionOccurred) {
        this.handlerData = handlerData;
        this.processedData = processedData;
        this.completeChainResult = completeChainResult;
        this.exceptionOccurred = exceptionOccurred;
    }

    public CompleteChainResult getCompleteChainResult() {
        return completeChainResult;
    }

    public void setCompleteChainResult(CompleteChainResult completeChainResult) {
        this.completeChainResult = completeChainResult;
    }

    @Override
    public String toString() {
        return "GlobalFallBackContext{" +
                "completeChainResult=" + completeChainResult +
                ", handlerData=" + handlerData +
                ", processedData=" + processedData +
                ", exceptionOccurred=" + exceptionOccurred +
                '}';
    }
}
