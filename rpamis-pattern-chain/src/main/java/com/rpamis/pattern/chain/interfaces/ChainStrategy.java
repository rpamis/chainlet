package com.rpamis.pattern.chain.interfaces;


import com.rpamis.pattern.chain.entity.ChainResult;

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
     * @param message message
     * @return ChainResult
     */
    default ChainResult init(Class<?> handlerClass, boolean processResult, String message) {
        return new ChainResult(handlerClass, processResult, message);
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
