package com.rpamis.chain.core.interfaces;


import com.rpamis.chain.core.entity.ChainResult;

/**
 * 责任链策略接口
 *
 * @author benym
 * @date 2023/3/7 18:10
 */
public interface ChainStrategy<T> {

    /**
     * 策略接口初始化
     *
     * @param handlerClass handlerClass
     * @param processResult processResult
     * @return ChainResult
     */
    default ChainResult init(Class<?> handlerClass, boolean processResult) {
        return new ChainResult(handlerClass, processResult);
    }

    /**
     * 执行对应返回策略
     *
     * @param handlerData handlerData
     * @param chain       chain
     * @param chainResult chainResult
     */
    void doStrategy(T handlerData, ChainPipeline<T> chain, ChainResult chainResult);
}
