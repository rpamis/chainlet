package com.rpamis.pattern.chain;


import com.rpamis.pattern.chain.entity.ChainResult;
import com.rpamis.pattern.chain.interfaces.ChainHandler;
import com.rpamis.pattern.chain.interfaces.ChainPipeline;
import com.rpamis.pattern.chain.interfaces.ChainStrategy;

/**
 * 抽象化责任链处理类Handler
 *
 * @param <T> <T>
 * @author benym
 * @date 2023/2/1 17:33
 */
public abstract class AbstractChainHandler<T> implements ChainHandler<T> {

    /**
     * handler链式处理
     *
     * @param handlerData handlerData
     * @param chain       chain
     * @param strategy    strategy
     */
    @Override
    public void handle(T handlerData, ChainPipeline<T> chain, ChainStrategy<T> strategy) {
        // 具体某个handler处理
        boolean processResult = process(handlerData);
        // 根据策略进行返回值包装
        ChainResult chainResult = strategy.init(this.getClass(), processResult);
        strategy.doStrategy(handlerData, chain, chainResult);
    }

    /**
     * 执行具体handler,true表示执行成功,false表示执行失败
     *
     * @param handlerData handlerData
     * @return boolean
     */
    protected abstract boolean process(T handlerData);
}
