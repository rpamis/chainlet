package com.rpamis.pattern.chain.entity;

/**
 * 责任链全局降级上下文
 *
 * @author benym
 * @date 2023/8/23 14:17
 */
public class GlobalFallBackContext<T> extends FallBackContext<T> {

    /**
     * 责任链最终结果实体
     */
    CompleteChainResult completeChainResult;

    public GlobalFallBackContext(T handlerData, CompleteChainResult completeChainResult, Boolean exceptionOccurred) {
        this.handlerData = handlerData;
        this.completeChainResult = completeChainResult;
        this.exceptionOccurred = exceptionOccurred;
    }

    public CompleteChainResult getCompleteChainResult() {
        return completeChainResult;
    }

    public void setCompleteChainResult(CompleteChainResult completeChainResult) {
        this.completeChainResult = completeChainResult;
    }
}
