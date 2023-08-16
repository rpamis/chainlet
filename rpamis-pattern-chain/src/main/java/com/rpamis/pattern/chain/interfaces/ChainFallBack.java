package com.rpamis.pattern.chain.interfaces;

/**
 * 责任链降级处理接口
 *
 * @author benym
 * @date 2023/8/16 16:14
 */
public interface ChainFallBack<T> {

    /**
     * 降级方法
     *
     * @param fallBackContext fallBackContext
     */
    void fallBack(T fallBackContext);
}
