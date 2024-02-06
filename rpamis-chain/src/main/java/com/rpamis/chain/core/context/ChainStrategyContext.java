package com.rpamis.chain.core.context;

import com.rpamis.chain.core.definition.ChainInnerPipeline;
import com.rpamis.chain.core.entities.ChainResult;

import java.util.List;

/**
 * 责任链执行策略上下文
 *
 * @author benym
 * @date 2023/12/19 17:47
 */
public class ChainStrategyContext<T> {
    /**
     * 责任链处理的数据
     */
    private T handlerData;

    /**
     * 责任链
     */
    private ChainInnerPipeline<T> chain;

    /**
     * 单个责任链Handler执行结果
     */
    private ChainResult chainResult;

    /**
     * 责任链存储结果list
     */
    private List<ChainResult> checkResults;

    public ChainStrategyContext(T handlerData, ChainInnerPipeline<T> chain, ChainResult chainResult, List<ChainResult> checkResults) {
        this.handlerData = handlerData;
        this.chain = chain;
        this.chainResult = chainResult;
        this.checkResults = checkResults;
    }

    public T getHandlerData() {
        return handlerData;
    }

    public void setHandlerData(T handlerData) {
        this.handlerData = handlerData;
    }

    public ChainInnerPipeline<T> getChain() {
        return chain;
    }

    public void setChain(ChainInnerPipeline<T> chain) {
        this.chain = chain;
    }

    public ChainResult getChainResult() {
        return chainResult;
    }

    public void setChainResult(ChainResult chainResult) {
        this.chainResult = chainResult;
    }

    public List<ChainResult> getCheckResults() {
        return checkResults;
    }

    public void setCheckResults(List<ChainResult> checkResults) {
        this.checkResults = checkResults;
    }
}
