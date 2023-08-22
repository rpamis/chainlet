package com.rpamis.pattern.chain.interfaces;

/**
 * With接口
 *
 * @author benym
 * @date 2023/8/21 15:09
 */
public interface With<T> extends Apply<T>, Builder<T> {

    /**
     * 责任链执行策略
     *
     * @param strategy strategy
     * @return ChainPipeline
     */
    With<T> strategy(ChainStrategy<T> strategy);

    /**
     * 责任链全局降级策略
     *
     * @param fallBack fallBack
     * @return ChainPipeline
     */
    With<T> globalFallback(ChainFallBack<T> fallBack);
}
