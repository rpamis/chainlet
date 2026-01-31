package com.rpamis.chainlet.core.fluent;

import com.rpamis.chainlet.core.definition.ChainStrategy;
import com.rpamis.chainlet.core.fallback.GlobalChainFallBack;
import com.rpamis.chainlet.core.strategy.StrategyKey;

/**
 * With接口
 *
 * @author benym
 * @since 2023/8/21 15:09
 */
public interface With<T> extends Builder<T> {

    /**
     * 责任链执行策略
     *
     * @param strategyKey strategyKey
     * @return ChainPipeline
     */
    With<T> strategy(StrategyKey strategyKey);

    /**
     * 责任链执行策略
     *
     * @param chainStrategy chainStrategy
     * @return ChainPipeline
     */
    With<T> strategy(ChainStrategy<T> chainStrategy);

    /**
     * 责任链全局降级策略
     *
     * @param fallBack fallBack
     * @return ChainPipeline
     */
    With<T> globalFallback(GlobalChainFallBack<T> fallBack);
}
