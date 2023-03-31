package com.benym.rpamis.pattern.chain.interfaces;


import com.benym.rpamis.pattern.chain.entity.ChainException;
import com.benym.rpamis.pattern.chain.entity.CompleteChainResult;

import java.io.IOException;

/**
 * 泛型责任链流水线接口
 *
 * @param <T> <T>
 * @date 2023/3/8 18:24
 * @author benym
 */
public interface ChainPipeline<T> {

    /**
     * 添加Handler处理
     *
     * @param handler 具体的Handler处理类
     * @return ChainPipeline<T>责任链流水线
     */
    ChainPipeline<T> addHandler(ChainHandler<T> handler);

    /**
     * 流水线执行Handler处理
     *
     * @param handlerData 需要处理的数据
     * @throws IOException    IO异常
     * @throws ChainException 责任链异常
     */
    void doHandler(T handlerData) throws IOException, ChainException;

    /**
     * 流水线执行Handler后的处理
     */
    default void afterHandler() {
    }

    /**
     * 责任链执行策略
     *
     * @param strategy strategy
     * @return ChainPipeline
     */
    ChainPipeline<T> strategy(ChainStrategy<T> strategy);

    /**
     * 责任链流水线执行入口
     *
     * @param handlerData handlerData
     * @return boolean
     * @throws ChainException ChainException
     */
    CompleteChainResult start(T handlerData) throws ChainException;
}
