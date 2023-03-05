package com.benym.rpamis.pattern.chain;

import java.io.IOException;
import java.util.List;

/**
 * 泛型责任链Handler接口
 *
 * @param <T> <T>
 * @author benym
 */
public interface ChainHandler<T> {

    /**
     * handler链式处理
     *
     * @param handlerData   handlerData
     * @param chain         chain
     * @param chainStrategy chainStrategy
     * @param checkResult checkResult
     * @return boolean
     */
    boolean handle(T handlerData, ChainPipline<T> chain, ChainStrategy chainStrategy, ThreadLocal<List<Boolean>> checkResult) throws IOException, ChainException;
}
