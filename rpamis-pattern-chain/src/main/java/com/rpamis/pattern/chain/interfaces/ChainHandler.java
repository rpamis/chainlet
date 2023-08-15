package com.rpamis.pattern.chain.interfaces;


import com.rpamis.pattern.chain.entity.ChainResult;

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
    default void handle(T handlerData, ChainPipeline<T> chain, ChainStrategy<T> strategy) {
        // 具体某个handler处理
        boolean processResult = this.process(handlerData);
        // 根据策略进行返回值包装
        ChainResult chainResult = strategy.init(this.getClass(), processResult, this.message());
        strategy.doStrategy(handlerData, chain, chainResult);
    }

    /**
     * 责任链处理器执行接口
     * 执行具体handler,true表示执行成功,false表示执行失败
     *
     * @param handlerData handlerData
     * @return boolean
     */
    boolean process(T handlerData);

    /**
     * 责任链处理器自定义提示消息
     *
     * @return String
     */
    default String message(){
        return "";
    }
}
