package com.benym.rpamis.pattern.chain;

import java.io.IOException;

/**
 * 泛型责任链流水线接口
 *
 * @param <T> <T>
 * @author benym
 */
public interface ChainPipline<T> {

    /**
     * 添加Handler处理
     *
     * @param handler 具体的Handler处理类
     * @return AbstractChainPipline<T>责任链流水线
     */
    AbstractChainPipeline<T> addHandler(AbstractChainHandler<T> handler) throws ChainException;

    /**
     * 流水线执行Handler处理
     *
     * @param handlerData 需要处理的数据
     */
    void doHandler(T handlerData) throws IOException, ChainException;

    /**
     * 流水线执行Handler后的处理
     */
    default void afterHandler() {}
}
