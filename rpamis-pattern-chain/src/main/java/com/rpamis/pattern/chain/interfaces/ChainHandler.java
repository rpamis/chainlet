package com.rpamis.pattern.chain.interfaces;


import com.rpamis.pattern.chain.entity.ChainException;

import java.io.IOException;

/**
 * 泛型责任链Handler接口
 *
 * @param <T> <T>
 * @date 2023/3/8 18:24
 * @author benym
 */
public interface ChainHandler<T> {

    /**
     * handler链式处理
     *
     * @param handlerData handlerData
     * @param chain       chain
     * @param strategy    strategy
     * @throws IOException    IOException
     * @throws ChainException ChainException
     */
    void handle(T handlerData, ChainPipeline<T> chain, ChainStrategy<T> strategy) throws IOException, ChainException;
}
