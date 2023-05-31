package com.rpamis.pattern.chain.interfaces;


/**
 * 泛型责任链Handler接口
 *
 * @param <T> <T>
 * @author benym
 * @date 2023/3/8 18:24
 */
public interface ChainHandler<T> {

    /**
     * handler链式处理
     *
     * @param handlerData handlerData
     * @param chain       chain
     * @param strategy    strategy
     */
    void handle(T handlerData, ChainPipeline<T> chain, ChainStrategy<T> strategy);
}
