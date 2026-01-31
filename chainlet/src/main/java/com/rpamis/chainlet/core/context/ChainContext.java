package com.rpamis.chainlet.core.context;

import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.definition.ChainInnerPipeline;
import com.rpamis.chainlet.core.definition.ChainStrategy;
import com.rpamis.chainlet.core.entities.ChainResult;

import java.io.Serializable;
import java.util.List;

/**
 * 责任链上下文
 *
 * @author benym
 * @since 2023/8/16 17:01
 */
public class ChainContext<T> implements Serializable {

    private static final long serialVersionUID = 4424283192522709548L;

    /**
     * 责任链处理的数据
     */
    private T handlerData;

    /**
     * 责任链
     */
    private ChainInnerPipeline<T> chain;

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

    public ChainContext(T handlerData, ChainInnerPipeline<T> chain, ChainStrategy<T> strategy, ChainHandler<T> chainHandler, List<ChainResult> checkResults) {
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

    public ChainInnerPipeline<T> getChain() {
        return chain;
    }

    public void setChain(ChainInnerPipeline<T> chain) {
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
}
