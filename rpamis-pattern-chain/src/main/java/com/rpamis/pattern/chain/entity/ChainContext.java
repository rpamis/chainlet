package com.rpamis.pattern.chain.entity;

import com.rpamis.pattern.chain.interfaces.ChainPipeline;
import com.rpamis.pattern.chain.interfaces.ChainStrategy;

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
    T handlerData;

    /**
     * 责任链
     */
    ChainPipeline<T> chain;

    /**
     * 责任链执行策略
     */
    ChainStrategy<T> strategy;

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

    @Override
    public String toString() {
        return "ChainContext{" +
                "handlerData=" + handlerData +
                ", chain=" + chain +
                ", strategy=" + strategy +
                '}';
    }
}
