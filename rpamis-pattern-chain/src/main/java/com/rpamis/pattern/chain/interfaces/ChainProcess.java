package com.rpamis.pattern.chain.interfaces;

/**
 *
 *
 * @author benym
 * @date 2023/7/30 11:53
 */
public interface ChainProcess<T> {

    /**
     *
     *
     * @param handlerData
     * @return
     */
    boolean process(T handlerData);
}
