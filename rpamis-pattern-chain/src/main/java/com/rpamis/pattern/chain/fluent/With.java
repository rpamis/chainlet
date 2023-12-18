package com.rpamis.pattern.chain.fluent;

import com.rpamis.pattern.chain.definition.ChainStrategy;
import com.rpamis.pattern.chain.definition.ChainFallBack;
import com.rpamis.pattern.chain.strategy.Strategy;

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
    With<T> strategy(Strategy strategy);

    /**
     * 责任链全局降级策略
     *
     * @param fallBack fallBack
     * @return ChainPipeline
     */
    With<T> globalFallback(ChainFallBack<T> fallBack);
}
