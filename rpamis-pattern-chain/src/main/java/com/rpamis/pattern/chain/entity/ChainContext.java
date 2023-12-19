package com.rpamis.pattern.chain.entity;

import com.rpamis.pattern.chain.definition.ChainHandler;
import com.rpamis.pattern.chain.definition.ChainPipeline;
import com.rpamis.pattern.chain.definition.ChainStrategy;

import java.util.List;

/**
 * 责任链上下文
 *
 * @author benym
 * @date 2023/8/16 17:01
 */
public class ChainContext<T> {

    /**
     * 责任链处理的数据
     */
    private T handlerData;

    /**
     * 责任链
     */
    private ChainPipeline<T> chain;

    /**
     * 责任链执行策略
     */
    private ChainStrategy<T> strategy;

    /**
     * 责任链具体执行器
     */
    private ChainHandler<T> chainHandler;

    /**
     * 责任链存储结果list
     */
    private List<ChainResult> checkResults;

    public ChainContext(T handlerData, ChainPipeline<T> chain, ChainStrategy<T> strategy, ChainHandler<T> chainHandler, List<ChainResult> checkResults) {
        this.handlerData = handlerData;
        this.chain = chain;
        this.strategy = strategy;
        this.chainHandler = chainHandler;
        this.checkResults = checkResults;
    }

    public T getHandlerData() {
        return handlerData;
    }

    public void setHandlerData(T handlerData) {
        this.handlerData = handlerData;
    }

    public ChainPipeline<T> getChain() {
        return chain;
    }

    public void setChain(ChainPipeline<T> chain) {
        this.chain = chain;
    }

    public ChainStrategy<T> getStrategy() {
        return strategy;
    }

    public void setStrategy(ChainStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public ChainHandler<T> getChainHandler() {
        return chainHandler;
    }

    public void setChainHandler(ChainHandler<T> chainHandler) {
        this.chainHandler = chainHandler;
    }

    public List<ChainResult> getCheckResults() {
        return checkResults;
    }

    public void setCheckResults(List<ChainResult> checkResults) {
        this.checkResults = checkResults;
    }

    @Override
    public String toString() {
        return "ChainContext{" +
                "handlerData=" + handlerData +
                ", chain=" + chain +
                ", strategy=" + strategy +
                ", chainHandler=" + chainHandler +
                ", checkResults=" + checkResults +
                '}';
    }
}
